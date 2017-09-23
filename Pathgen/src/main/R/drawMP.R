plotProfile <- function(profileName, inverted = FALSE, wheelbaseDiameter, centerToFront, centerToBack, centerToSide, startY = 0, startPos = c(-1,-1,-1,-1,-1), usePosition = TRUE){
  left <- read.csv(paste("../../../../calciferLeft",profileName,"Profile.csv",sep=""), header=FALSE)
  right <- read.csv(paste("../../../../calciferRight",profileName,"Profile.csv",sep=""), header=FALSE)
  startingCenter <- c(startY, centerToBack)
  left$V1[1] <- 0
  left$V2[1] <- 0
  left$V3[1] <- left$V3[2]
  right$V1[1] <- 0
  right$V2[1] <- 0
  right$V3[1] <- right$V3[2]
  #Position,Velocity,Delta t, Elapsed time
  left$V4 <- (0:(length(left$V1)-1))*left$V3[1]
  right$V4 <- (0:(length(right$V1)-1))*right$V3[1]
  #Time, Left X, Left Y, Right X, Right Y
  out <- array(dim=c(length(left$V1),5))
  if(identical(startPos, c(-1,-1,-1,-1,-1))){
    out[1,]<-c(0, startingCenter[2], (startingCenter[1]+wheelbaseDiameter/2.), startingCenter[2], (startingCenter[1]-wheelbaseDiameter/2.))
  } else {
    out[1,]<-startPos
  }
  
  for(i in 2:length(left$V4)){
    #Get the angle the robot is facing.
    perpendicular <- angleBetween(leftX = out[i-1,2], leftY = out[i-1,3], rightX = out[i-1,4], rightY = out[i-1,5])-pi/2
    
    #Add the change in time
    out[i,1] <- out[i-1,1]+left$V3[i]
    
    #Figure out linear change for each side using position or velocity
    if (usePosition){
      deltaLeft <- left$V1[i] - left$V1[i-1]
      deltaRight <- right$V1[i] - right$V1[i-1]
    } else {
      deltaLeft <- left$V2[i]*left$V3[i]
      deltaRight <- right$V2[i]*left$V3[i]
    }
    
    # Invert the change if nessecary
    if (inverted){
      deltaLeft <- -deltaLeft
      deltaRight <- -deltaRight
    }
    
    #So in this next part, we figure out the turning center of the robot
    #and the angle it turns around that center. Note that the turning center is
    #often outside of the robot.
    
    #Calculate how much we turn first, because if theta = 0, turning center is infinitely far away and can't be calcualted.
    theta <- (deltaLeft - deltaRight)/wheelbaseDiameter
    
    # If theta is 0, we're going straight and need to treat it as a special case.
    if (identical(theta, 0)){
      
      #If inverted, swap which wheel gets which input
      if(inverted){
        out[i, 2] <- out[i-1,2]+deltaRight*cos(perpendicular)
        out[i, 3] <- out[i-1,3]+deltaRight*sin(perpendicular)
        out[i, 4] <- out[i-1,4]+deltaLeft*cos(perpendicular)
        out[i, 5] <- out[i-1,5]+deltaLeft*sin(perpendicular)
      } else {
        out[i, 2] <- out[i-1,2]+deltaLeft*cos(perpendicular)
        out[i, 3] <- out[i-1,3]+deltaLeft*sin(perpendicular)
        out[i, 4] <- out[i-1,4]+deltaRight*cos(perpendicular)
        out[i, 5] <- out[i-1,5]+deltaRight*sin(perpendicular)
      }
    } else {
      
      #We do this with sectors, so this is the radius of the turning circle for the
      #left and right sides. They just differ by the diameter of the wheelbase.
      rightR <- (wheelbaseDiameter/2) * (deltaLeft + deltaRight) / (deltaLeft - deltaRight) - wheelbaseDiameter/2
      leftR <- rightR + wheelbaseDiameter
      
      #This is the angle for the vector pointing towards the new position of each
      #wheel.
      #To understand why this formula is correct, overlay isoclese triangles on the sectors
      vectorTheta <- (pi - theta)/2 - (pi/2 - perpendicular)
      
      #The is the length of the vector pointing towards the new position of each
      #wheel divided by the radius of the turning circle.
      vectorDistanceWithoutR <- sin(theta)/sin((pi-theta)/2)
      
      #If inverted, swap which wheel gets which input
      if(inverted){
        out[i, 2] <- out[i-1,2]+vectorDistanceWithoutR*rightR*cos(vectorTheta)
        out[i, 3] <- out[i-1,3]+vectorDistanceWithoutR*rightR*sin(vectorTheta)
        out[i, 4] <- out[i-1,4]+vectorDistanceWithoutR*leftR*cos(vectorTheta)
        out[i, 5] <- out[i-1,5]+vectorDistanceWithoutR*leftR*sin(vectorTheta)
      } else {
        out[i, 2] <- out[i-1,2]+vectorDistanceWithoutR*leftR*cos(vectorTheta)
        out[i, 3] <- out[i-1,3]+vectorDistanceWithoutR*leftR*sin(vectorTheta)
        out[i, 4] <- out[i-1,4]+vectorDistanceWithoutR*rightR*cos(vectorTheta)
        out[i, 5] <- out[i-1,5]+vectorDistanceWithoutR*rightR*sin(vectorTheta)
      }
    }
  }
  return(out)
}

drawProfile <- function (coords, centerToFront, centerToBack, wheelbaseDiameter, clear=TRUE, linePlot = TRUE){
  
  if (clear){
    if (linePlot){
      plot(coords[,2],coords[,3], type="l", col="Green", ylim=c(-16, 16),xlim = c(0,54), asp=1)
    } else {
      plot(coords[,2],coords[,3], col="Green", ylim=c(-16, 16), xlim = c(0,54), asp=1)
    }
    field <- read.csv("field.csv")
    #Strings are read as factors by default, so we need to do this to make it read them as strings
    field$col <- as.character(field$col)
    for (i in 1:length(field$x1)){
      lines(c(field$x1[i], field$x2[i]), c(field$y1[i], field$y2[i]), col=field$col[i])
    }
  } else {
    if (linePlot){
      lines(coords[,2],coords[,3],col="Green")
    } else {
      points(coords[,2],coords[,3],col="Green")
    }
  }
  if (linePlot){
    lines(coords[,4],coords[,5],col="Red")
  } else {
    points(coords[,4],coords[,5],col="Red")
  }
}

angleBetween <- function(leftX, leftY, rightX, rightY){
  deltaX <- leftX-rightX
  deltaY <- leftY-rightY
  if (identical(deltaX, 0)){
    ans <- pi/2
  } else {
    #Pretend it's first quadrant because we manually determine quadrants
    ans <- atan(abs(deltaY/deltaX))
  }
  if (deltaY > 0){
    if (deltaX > 0){
      #If it's actually quadrant 1
      return(ans)
    }else {
      #quadrant 2
      return(pi - ans)
    }
    return(ans)
  } else {
    if (deltaX > 0){
      #quadrant 4
      return(-ans)
    }else {
      #quadrant 3
      return(-(pi - ans))
    }
  }
}

drawRobot <- function(robotFile, robotPos){
  theta <- angleBetween(leftX = robotPos[2], leftY = robotPos[3], rightX = robotPos[4], rightY = robotPos[5])
  perp <- theta - pi/2
  robotCenter <- c((robotPos[2]+robotPos[4])/2.,(robotPos[3]+robotPos[5])/2.)
  robot <- read.csv(robotFile)
  rotMatrix <- matrix(c(cos(perp), -sin(perp), sin(perp), cos(perp)), nrow=2, ncol=2, byrow=TRUE)
  
  point1s <- rotMatrix %*% matrix(c(robot$x1, robot$y1), nrow = 2, ncol = length(robot$x1), byrow = TRUE) 
  point1s <- point1s + c(robotCenter[1], robotCenter[2])
  
  point2s <- rotMatrix %*% matrix(c(robot$x2, robot$y2), nrow = 2, ncol = length(robot$x1), byrow = TRUE) 
  point2s <- point2s + c(robotCenter[1], robotCenter[2])
  
  #Interleave the point1s and point2s so lines() draws them correctly.
  xs <- c(rbind(point1s[1,], point2s[1,]))
  ys <- c(rbind(point1s[2,], point2s[2,]))
  
  lines(x=xs, y=ys, col="Blue")
}

wheelbaseDiameter <- 26./12.
centerToFront <- (27./2.)/12.
centerToBack <- (27./2.+3.25)/12.
centerToSide <- (29./2.+3.25)/12.

#out <- plotProfile(profileName = "BlueRight", inverted = FALSE, wheelbaseDiameter = wheelbaseDiameter, centerToFront = centerToFront,centerToBack =  centerToBack,centerToSide = centerToSide, startY= -10.3449+centerToSide, usePosition = TRUE)
#drawProfile(coords=out, centerToFront=centerToFront, centerToBack=centerToBack, wheelbaseDiameter = wheelbaseDiameter, clear = TRUE, linePlot = TRUE)
#tmp <- out[length(out[,1]),]
#drawRobot("robot.csv", tmp)
#out2 <- plotProfile(profileName = "BlueBackup",inverted = TRUE,wheelbaseDiameter =  wheelbaseDiameter,centerToFront = centerToFront,centerToBack = centerToBack,centerToSide = centerToSide,startPos = tmp)
#drawProfile(coords = out2, centerToFront = centerToFront, centerToBack = centerToBack, wheelbaseDiameter = wheelbaseDiameter, clear = FALSE)
#tmp2 <- out2[length(out2[,1]),]
#drawRobot("robot.csv", out2[length(out2[,1]),])
#out3 <- plotProfile(profileName = "LoadingToLoading",inverted = FALSE,wheelbaseDiameter =  wheelbaseDiameter,centerToFront = centerToFront,centerToBack = centerToBack,centerToSide = centerToSide,startPos = tmp2)
#drawProfile(coords = out3, centerToFront = centerToFront, centerToBack = centerToBack, wheelbaseDiameter = wheelbaseDiameter, clear = FALSE)
#drawRobot("robot.csv", out3[length(out3[,1]),])

out <- plotProfile(profileName = "BlueLeft", inverted = FALSE, wheelbaseDiameter = wheelbaseDiameter, centerToFront = centerToFront,centerToBack =  centerToBack,centerToSide = centerToSide, startY= 10.3449-centerToSide, usePosition = TRUE)
drawProfile(coords=out, centerToFront=centerToFront, centerToBack=centerToBack, wheelbaseDiameter = wheelbaseDiameter, clear = TRUE, linePlot = TRUE)
tmp <- out[length(out[,1]),]
drawRobot("robot.csv", tmp)
out2 <- plotProfile(profileName = "RedBackup",inverted = TRUE,wheelbaseDiameter =  wheelbaseDiameter,centerToFront = centerToFront,centerToBack = centerToBack,centerToSide = centerToSide,startPos = tmp)
drawProfile(coords = out2, centerToFront = centerToFront, centerToBack = centerToBack, wheelbaseDiameter = wheelbaseDiameter, clear = FALSE)
tmp2 <- out2[length(out2[,1]),]
drawRobot("robot.csv", out2[length(out2[,1]),])
out3 <- plotProfile(profileName = "BoilerToLoading",inverted = FALSE,wheelbaseDiameter =  wheelbaseDiameter,centerToFront = centerToFront,centerToBack = centerToBack,centerToSide = centerToSide,startPos = tmp2)
drawProfile(coords = out3, centerToFront = centerToFront, centerToBack = centerToBack, wheelbaseDiameter = wheelbaseDiameter, clear = FALSE)
drawRobot("robot.csv", out3[length(out3[,1]),])