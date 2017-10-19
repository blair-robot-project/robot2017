#Simple helper conversion methods
rad2deg <- function(rad) {(rad * 180) / (pi)}
deg2rad <- function(deg) {(deg * pi) / (180)}

#Calculate the effective wheelbase for a given delta left, right, and angle
calcWheelbase <- function(deltaLeft, deltaRight, deltaAngle){
  return((deltaLeft-deltaRight)/deltaAngle);
}

#Smooths a value while taking its derivative with respect to time.
smoothDerivative <- function(value, timeMillis, n){
  smoothed <- (value[(n+1):length(value)] - value[1:(length(value)-n)])/((timeMillis[(n+1):length(timeMillis)] - timeMillis[1:(length(timeMillis)-n)])/1000);
  return(c(rep(0, ceiling(n/2)), smoothed, rep(0, floor(n/2))));
}

#Plot the effective wheelbase against a bunch of different things
plotWheelVsVel <- function(leftPos, rightPos, rawAngleDegrees, timeMillis, angularVelThreshRad, smoothingConst){
  #Convert because degrees suuuuck
  rawAngle <- deg2rad(rawAngleDegrees)
  
  #Smooth values and get velocities
  angular <- smoothDerivative(rawAngle, timeMillis, smoothingConst)
  left <- smoothDerivative(leftPos, timeMillis, smoothingConst)
  right <- smoothDerivative(rightPos, timeMillis, smoothingConst)
  
  #find effective wheelbase
  wheelbase <- calcWheelbase(left, right, angular)
  
  #Filter out low angular vel points
  combined <- cbind(angular, wheelbase, (left+right)/2)
  combinedAngular <- subset(combined, combined[,1] > angularVelThreshRad)
  
  #Find the mean wheelbase, weighted by angular vel because higher angular vel decreases variance
  avgWheelbase = weighted.mean(x=combinedAngular[,2], w=combinedAngular[,1], na.rm=TRUE)
  
  #plot wheelbase vs angular vel
  plot(x=combinedAngular[,1], y=combinedAngular[,2], xlab="Angular Velocity (rad/sec)", ylab="Effective wheelbase diameter (feet)", main="Effective Wheelbase Diameter vs. Angular Velocity")
  abline(a=avgWheelbase, b=0, col='green')
  
  #plot wheelbase vs linear vel
  plot(x=combinedAngular[,3], y=combinedAngular[,2], xlab="Linear Velocity (feet/sec)", ylab="Effective wheelbase diameter (feet)", main="Effective Wheelbase Diameter vs. Linear Velocity")
  abline(a=avgWheelbase, b=0, col='green')
  
  #Plot wheelbase vs turn radius
  plot(x=combinedAngular[,3]/combinedAngular[,1], y=combinedAngular[,2]-2.0833, xlab="Turn Radius (feet)", ylab="Error in wheelbase diameter (feet)", main="Error in Wheelbase Diameter vs. Turn Radius")
  abline(a=avgWheelbase, b=0, col='green')
  
  return(avgWheelbase)
}

#A pose estimation algorithm that assumes the left and right sides have equal scrub, in opposite directions
equalScrubPoseEstimation <- function(leftPos, rightPos, rawAngleDegrees, timeMillis, angularVelThreshRad){
  #Convert because degrees suuuuck
  rawAngle <- deg2rad(rawAngleDegrees)
  
  #Set up output array
  out <- array(dim=c(length(timeMillis),8))
  colnames(out)<-c("X","Y","leftX","leftY","rightX","rightY","time","wheelbase")
  out[1,] <- c(leftPos[1],rightPos[1],NA,NA,NA,NA,timeMillis[1],NA)
  
  #Loop through each logged tic, calculating pose iteratively
  for(i in 2:length(timeMillis)){
    #Find change in time, in seconds
    deltaTime <- (timeMillis[i] - out[i-1,7])/1000
    
    #Directly find change in position and angle
    deltaLeft <- leftPos[i]-leftPos[i-1]
    deltaRight <- rightPos[i]-rightPos[i-1]
    deltaTheta <- rawAngle[i]-rawAngle[i-1]
    
    #Get the effective wheelbase for this iteration
    wheelbase <- calcWheelbase(deltaLeft, deltaRight, deltaTheta)
    
    #Average
    avgMoved <- (deltaLeft+deltaRight)/2
    
    #The angle of the movement vector
    angle <- rawAngle[i-1]-(deltaTheta/2)

    if (deltaTheta == 0){
      #If we're driving straight, we know the magnitude is just the average of the two sides
      out[i,] <- c(out[i-1,1]+avgMoved*cos(angle),out[i-1,2]+avgMoved*sin(angle), NA, NA, NA, NA, timeMillis[i],NA)
    } else {
      #Magnitude of movement vector is 2*r*sin(deltaTheta/2), but r is just avg/deltaTheta
      mag <- 2*(avgMoved/deltaTheta)*sin(deltaTheta/2)
      
      #Vector decomposition
      x <- out[i-1,1]+mag*cos(angle)
      y <- out[i-1,2]+mag*sin(angle)
      
      #Only log left and right wheel positions if the angular vel is above threshhold
      if (deltaTheta/deltaTime < angularVelThreshRad){
        out[i,] <- c(x, y, NA,NA,NA,NA, timeMillis[i],NA)
      } else {
        out[i,] <- c(x, y, x+wheelbase/2*cos(angle+pi/2), y+wheelbase/2*sin(angle+pi/2), x+wheelbase/2*cos(angle-pi/2), y+wheelbase/2*sin(angle-pi/2), timeMillis[i],wheelbase)
      }
    }
  }
  
  #Plot results, with fake wheelbase, only showing points within 1 actual wheelbase of the path
  plot(out[,1], out[,2], t="l", xlim = c(min(out[,1], na.rm = TRUE)-3,max(out[,1],na.rm = TRUE)+3), ylim=c(min(out[,2], na.rm = TRUE)-3,max(out[,2], na.rm=TRUE)+3), xlab="X position (Feet)", ylab="Y position (Feet)", main="Equal Scrub Pose Estimation Algorithm", asp=1)
  lines(out[,3], out[,4], col="Green")
  lines(out[,5], out[,6], col="Red")
  
  return(out)
}

#A pose estimation algorithm that ignores the worse encoder reading.
ignoreWorstPoseEstimation <- function(leftPos, rightPos, rawAngleDegrees, timeMillis, actualWheelbase){
  #Convert because degrees suuuuck
  rawAngle <- deg2rad(rawAngleDegrees)
  
  #Set up output array
  out <- array(dim=c(length(timeMillis),7))
  colnames(out) <- c("X","Y","leftX","leftY","rightX","rightY","time")
  angle <- rawAngle[1]
  out[1,] <- c(leftPos[1],rightPos[1],actualWheelbase/2*cos(angle+pi/2), actualWheelbase/2*sin(angle+pi/2), actualWheelbase/2*cos(angle-pi/2), actualWheelbase/2*sin(angle-pi/2),timeMillis[1])
  
  #Loop through each logged tic, calculating pose iteratively
  for(i in 2:length(timeMillis)){
    
    #Directly find change in position and angle
    deltaLeft <- leftPos[i]-leftPos[i-1]
    deltaRight <- rightPos[i]-rightPos[i-1]
    deltaTheta <- rawAngle[i]-rawAngle[i-1]
    
    #The angle of the movement vector
    angle <- rawAngle[i-1]-(deltaTheta/2)

    #Take the side that slipped more and recalculate it from the wheelbase, change in angle, and other side's change
    if (deltaTheta < (deltaLeft - deltaRight) / actualWheelbase) {
      if (deltaLeft > 0) {
        deltaLeft = deltaRight + actualWheelbase * deltaTheta;
      } else {
        deltaRight = deltaLeft - actualWheelbase * deltaTheta;
      }
    } else if (deltaTheta > (deltaLeft - deltaRight) / actualWheelbase) {
      if (deltaLeft < 0) {
        deltaLeft = deltaRight + actualWheelbase * deltaTheta;
      } else {
        deltaRight = deltaLeft - actualWheelbase * deltaTheta;
      }
    }
    
    #Calculate average after recalculating one of the sides
    avgMoved <- (deltaLeft+deltaRight)/2
    
    #If we're driving straight, the magnitude is just the average of the two sides
    if (deltaTheta == 0){
      out[i,] <- c(out[i-1,1]+avgMoved*cos(angle),out[i-1,2]+avgMoved*sin(angle), x+actualWheelbase/2*cos(angle+pi/2), y+actualWheelbase/2*sin(angle+pi/2), x+actualWheelbase/2*cos(angle-pi/2), y+actualWheelbase/2*sin(angle-pi/2), timeMillis[i])
    } else {
      #If the sides moved the same distance but the angle changed, the radius is just the sector length over the angle
      if (deltaLeft-deltaRight == 0){
        r <- avgMoved/deltaTheta;
      } else {
        #If they moved a different distance, do a more complicated equation (may be the same as the other one, not doing the math yet)
        r <- actualWheelbase / 2. * (deltaLeft + deltaRight) / (deltaLeft - deltaRight);
      }
      mag <- 2. * r * sin(deltaTheta / 2.);
      
      #Vector decomposition
      x <- out[i-1,1]+mag*cos(angle)
      y <- out[i-1,2]+mag*sin(angle)
      out[i,] <- c(x, y, x+actualWheelbase/2*cos(angle+pi/2), y+actualWheelbase/2*sin(angle+pi/2), x+actualWheelbase/2*cos(angle-pi/2), y+actualWheelbase/2*sin(angle-pi/2), timeMillis[i])
    }
  }
  
  #Plot results, with real wheelbase
  plot(out[,1], out[,2], t="l", xlim = c(min(out[,1], out[,3], out[,5]),max(out[,1], out[,3], out[,5])), ylim=c(min(out[,2], out[,4], out[,6]),max(out[,2], out[,4], out[,6])), xlab="X position (Feet)", ylab="Y position (Feet)", main="Ignore-Worst Pose Estimation Algorithm",asp=1)
  lines(out[,3], out[,4], col="Green")
  lines(out[,5], out[,6], col="Red")
  
  return(out)
}

#A pose estimation algorithm that only uses the encoders
encoderOnlyPoseEstimation <- function(leftPos, rightPos, startingAngleDegrees, timeMillis, fakeWheelbase){
  #Convert because degrees suuuuck
  startingAngle <- deg2rad(startingAngleDegrees)
  
  wheelRadius <- fakeWheelbase/2
  
  #Set up output array
  out <- array(dim=c(length(timeMillis), 8))
  colnames(out) <- c("X","Y","leftX","leftY","rightX","rightY","angle","time")
  out[1,] <- c(leftPos[1],rightPos[1],wheelRadius*cos(startingAngle+pi/2), wheelRadius*sin(startingAngle+pi/2), wheelRadius*cos(startingAngle-pi/2), wheelRadius*sin(startingAngle-pi/2),startingAngle,timeMillis[1])
  
  #Loop through each logged tic, calculating pose iteratively
  for(i in 2:length(timeMillis)){

    #Directly find change in position and angle
    deltaLeft <- leftPos[i]-leftPos[i-1]
    deltaRight <- rightPos[i]-rightPos[i-1]
    
    #Average
    avgMoved <- (deltaLeft+deltaRight)/2
    
    #Points in the direction the robot is facing at the start of tic
    perpendicular <- out[i-1,7]
    
    #The angle of the sector the path is tracing
    theta <- (deltaLeft - deltaRight)/fakeWheelbase
    
    #If not turning, magnitude is just the average moved
    if(theta == 0){
      mag <- avgMoved
    } else {
      #If turning, calculate the radius and use that to get the magnitude of the turn
      mag <- 2*(avgMoved/theta)*sin(theta/2)
    }
    
    angle <- perpendicular + (theta/2)
    x <- out[i-1,1]+mag*cos(angle)
    y <- out[i-1,2]+mag*sin(angle)
    newHeading <- perpendicular + theta
    out[i,] <- c(x,y,x+wheelRadius*cos(newHeading+pi/2), y+wheelRadius*sin(newHeading+pi/2), x+wheelRadius*cos(newHeading-pi/2), y+wheelRadius*sin(newHeading-pi/2), newHeading, timeMillis[i])
  }
  
  #Plot results, with fake wheelbase
  plot(out[,1], out[,2], t="l", xlim = c(min(out[,1], out[,3], out[,5]),max(out[,1], out[,3], out[,5])), ylim=c(min(out[,2], out[,4], out[,6]),max(out[,2], out[,4], out[,6])), xlab="X position (Feet)", ylab="Y position (Feet)", main="Encoder-only Pose Estimation Algorithm",asp=1)
  lines(out[,3], out[,4], col="Green")
  lines(out[,5], out[,6], col="Red")
  
  return(out)
}

#Make all the graphs, for wheelbase and all 3 algorithms, using the average effective wheelbase for encoder-only
doEverything <- function(leftPos, rightPos, rawAngleDegrees, timeMillis, angularVelThreshRad, smoothingConst, actualWheelbase){
  avg <- plotWheelVsVel(leftPos, rightPos, rawAngleDegrees, timeMillis, angularVelThreshRad, smoothingConst)
  equalScrubPoseEstimation(leftPos, rightPos, rawAngleDegrees, timeMillis, angularVelThreshRad)
  ignoreWorstPoseEstimation(leftPos, rightPos, rawAngleDegrees, timeMillis, actualWheelbase)
  encoderOnlyPoseEstimation(leftPos=leftPos, rightPos=rightPos, startingAngleDegrees=rawAngleDegrees[1], timeMillis=timeMillis, fakeWheelbase=avg)
}