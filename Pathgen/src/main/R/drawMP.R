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
    theta <- angleBetween(leftX = out[i-1,2], leftY = out[i-1,3], rightX = out[i-1,4], rightY = out[i-1,5])
    
    out[i,1] <- out[i-1,1]+left$V3[i]

    if (usePosition){
      deltaLeft <- left$V1[i] - left$V1[i-1]
      deltaRight <- right$V1[i] - right$V1[i-1]
    } else {
      deltaLeft <- left$V2[i]*left$V3[i]
      deltaRight <- right$V2[i]*left$V3[i]
    }
    
    if (inverted){
      deltaLeft <- -deltaLeft
      deltaRight <- -deltaRight
    }
    
    perpendicular <- theta - pi/2
    
    if(inverted){
      out[i, 2] <- out[i-1,2]+deltaRight*round(cos(perpendicular), digits = 3)
      out[i, 3] <- out[i-1,3]+deltaRight*round(sin(perpendicular), digits = 3)
      out[i, 4] <- out[i-1,4]+deltaLeft*round(cos(perpendicular), digits = 3)
      out[i, 5] <- out[i-1,5]+deltaLeft*round(sin(perpendicular), digits = 3)
    } else {
      out[i, 2] <- out[i-1,2]+deltaLeft*round(cos(perpendicular), digits = 3)
      out[i, 3] <- out[i-1,3]+deltaLeft*round(sin(perpendicular), digits = 3)
      out[i, 4] <- out[i-1,4]+deltaRight*round(cos(perpendicular), digits = 3)
      out[i, 5] <- out[i-1,5]+deltaRight*round(sin(perpendicular), digits = 3)
    }
  }
  return(out)
}

drawProfile <- function (coords, centerToFront, centerToBack, wheelbaseDiameter, clear=TRUE, linePlot = TRUE){
  robotPos <- coords[length(coords[,1]),]
  theta <- angleBetween(leftX = robotPos[2], leftY = robotPos[3], rightX = robotPos[4], rightY = robotPos[5])
  
  perpendicular <- theta - pi/2
  wheelToEdge <- centerToSide - wheelbaseDiameter/2.
  print(c("wheelToEdge", wheelToEdge))
  leftEdge <- c(robotPos[2] + wheelToEdge*cos(theta), robotPos[3] + wheelToEdge*sin(theta))
  rightEdge <- c(robotPos[4] + wheelToEdge*cos(theta+pi), robotPos[5] + wheelToEdge*sin(theta+pi))
  edgeToFront <- c(centerToFront*cos(perpendicular), centerToFront*sin(perpendicular))
  edgeToBack <- c(centerToBack*cos(perpendicular+pi), centerToFront*sin(perpendicular+pi))
  
  leftFront <- leftEdge + edgeToFront
  rightFront <- rightEdge + edgeToFront
  leftBack <- leftEdge + edgeToBack
  rightBack <- rightEdge + edgeToBack
  
  if (clear){
    if (linePlot){
      plot(coords[,2],coords[,3], type="l", col="Green", ylim=c(-13.5, 13.5),xlim = c(0,54), asp=1)
    } else {
      plot(coords[,2],coords[,3], col="Green", ylim=c(-13.5, 13.5),xlim = c(0,54), asp=1)
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
  lines(c(rightFront[1],leftFront[1]),c(rightFront[2],leftFront[2]), col="Blue")
  lines(c(rightBack[1],rightFront[1]),c(rightBack[2],rightFront[2]), col="Blue")
  lines(c(leftFront[1],leftBack[1]),c(leftFront[2],leftBack[2]), col="Blue")
  lines(c(leftBack[1],rightBack[1]),c(leftBack[2],rightBack[2]), col="Blue")
  endCenter <- c((robotPos[2]+robotPos[4])/2.,(robotPos[3]+robotPos[5])/2.)
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
  for (i in 1:length(robot$x1)){
    point1 <- rotMatrix%*%matrix(c(robot$x1[i], robot$y1[i]), nrow=2, ncol=1, byrow=TRUE)
    point1 <- c(point1[1,1]+robotCenter[1], point1[2,1]+robotCenter[2])
    point2 <- rotMatrix%*%matrix(c(robot$x2[i], robot$y2[i]), nrow=2, ncol=1, byrow=TRUE)
    point2 <- c(point2[1,1]+robotCenter[1], point2[2,1]+robotCenter[2])
    lines(c(point1[1], point2[1]), c(point1[2], point2[2]), col="Blue")
  }
}

wheelbaseDiameter <- 26./12.
centerToFront <- (27./2.)/12.
centerToBack <- (27./2.+3.25)/12.
centerToSide <- (29./2.+3.25)/12.
out <- plotProfile(profileName = "Right", inverted = FALSE, wheelbaseDiameter = wheelbaseDiameter, centerToFront = centerToFront,centerToBack =  centerToBack,centerToSide = centerToSide, startY=-(10.3449-centerToSide))
#out <- plotProfile(profileName = "Left", inverted = FALSE, wheelbaseDiameter = wheelbaseDiameter, centerToFront = centerToFront,centerToBack =  centerToBack,centerToSide = centerToSide, startY= 10.3449-centerToSide, usePosition = TRUE)
drawProfile(coords=out, centerToFront=centerToFront, centerToBack=centerToBack, wheelbaseDiameter = wheelbaseDiameter, linePlot = TRUE)
tmp <- out[length(out[,1]),]
#drawRobot("robot.csv", tmp)
out2 <- plotProfile(profileName = "BlueBackup",inverted = TRUE,wheelbaseDiameter =  wheelbaseDiameter,centerToFront = centerToFront,centerToBack = centerToBack,centerToSide = centerToSide,startPos = tmp)
#drawProfile(coords = out2, centerToFront = centerToFront, centerToBack = centerToBack, wheelbaseDiameter = wheelbaseDiameter, clear = FALSE)
