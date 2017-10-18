rad2deg <- function(rad) {(rad * 180) / (pi)}
deg2rad <- function(deg) {(deg * pi) / (180)}

calcWheelbase <- function(left, right, angle){
  return((left-right)/angle);
}

smoothDerivative <- function(value, timeMillis, n){
  smoothed <- (value[(n+1):length(value)] - value[1:(length(value)-n)])/((timeMillis[(n+1):length(timeMillis)] - timeMillis[1:(length(timeMillis)-n)])/1000);
  return(c(rep(0, ceiling(n/2)), smoothed, rep(0, floor(n/2))));
}

plotWheelVsVel <- function(leftPos, rightPos, rawAngleDegrees, timeMillis, angularVelThreshRad, smoothingConst){
  rawAngle <- deg2rad(rawAngleDegrees)
  
  #Smooth values
  angular <- smoothDerivative(rawAngle, timeMillis, smoothingConst)
  left <- smoothDerivative(leftPos, timeMillis, smoothingConst)
  right <- smoothDerivative(rightPos, timeMillis, smoothingConst)
  
  #find effective wheelbase
  wheelbase <- calcWheelbase(left, right, angular)
  
  #Filter out low angular vel points
  combined <- cbind(angular, wheelbase, (left+right)/2)
  combinedAngular <- subset(combined, combined[,1] > angularVelThreshRad)
  
  #Find the mean wheelbase, weighted by angular vel 
  avgWheelbase = weighted.mean(x=combinedAngular[,2], w=combinedAngular[,1], na.rm=TRUE)
  
  #plot angular
  plot(x=combinedAngular[,1], y=combinedAngular[,2], xlab="Angular Velocity (rad/sec)", ylab="Effective wheelbase diameter (feet)", main="Effective Wheelbase Diameter vs. Angular Velocity")
  abline(a=avgWheelbase, b=0, col='green')
  
  #plot linear
  plot(x=combinedAngular[,3], y=combinedAngular[,2], xlab="Linear Velocity (feet/sec)", ylab="Effective wheelbase diameter (feet)", main="Effective Wheelbase Diameter vs. Linear Velocity")
  abline(a=avgWheelbase, b=0, col='green')
  
  #Plot turn radius
  plot(x=combinedAngular[,3]/combinedAngular[,1], y=combinedAngular[,2]-2.0833, xlab="Turn Radius (feet)", ylab="Error in wheelbase diameter (feet)", main="Error in Wheelbase Diameter vs. Turn Radius")
  abline(a=avgWheelbase-2.0833, b=0, col='green')
  
  sumLeft <- 0
  out <- array(dim=c(length(angular),7))
  colnames(out)<-c("X","Y","leftX","leftY","rightX","rightY","time")
  out[1,] <- c(leftPos[1],rightPos[1],NA,NA,NA,NA,timeMillis[1])
  for(i in 2:length(angular)){
    deltaTime <- (timeMillis[i] - out[i-1,7])/1000
    deltaLeft <- leftPos[i]-leftPos[i-1]
    deltaRight <- rightPos[i]-rightPos[i-1]
    deltaTheta <- rawAngle[i]-rawAngle[i-1]
    avgMoved <- (deltaLeft+deltaRight)/2
    
    sumLeft <- sumLeft + deltaLeft
    if (deltaTheta == 0){
      out[i,] <- c(out[i-1,1]+avgMoved*cos(rawAngle[i]),out[i-1,2]+avgMoved*sin(rawAngle[i]), NA, NA, NA, NA, timeMillis[i])
    } else {
      angle <- rawAngle[i-1]-(deltaTheta/2)
      mag <- 2*(avgMoved/deltaTheta)*sin(deltaTheta/2)
      out[i,] <- c(out[i-1,1]+mag*cos(angle), out[i-1,2]+mag*sin(angle), NA, NA, NA, NA, timeMillis[i])
    }
  }
  
  plot(out[,1], out[,2], t="l")
  
  return(sumLeft)
}