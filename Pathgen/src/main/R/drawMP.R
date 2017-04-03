plotProfile <- function(profileName, inverted, wheelbaseDiameter, centerToFront, centerToBack, centerToSide, startY = 0, startPos = c(-1,-1,-1,-1,-1)){
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
  #out[1,]<-c(0,0,0,0,0)
  #out[1,] <- c (5.50,10.583184,4.343607,8.696782,3.244063)
  forward <- 1
  for(i in 2:length(left$V4)){
    if(identical(out[i-1,2]-out[i-1,4],0)){
      slope <- 0
      forward <- -forward
    } else{ 
      if(identical(out[i-1,3]-out[i-1,5],0)){
        slope <- forward
      } else {
        slope <- -1/((out[i-1,3]-out[i-1,5])/(out[i-1,2]-out[i-1,4]))
      }
    }
    out[i,1] <- out[i-1,1]+left$V3[i]
    #left$V2[i]
    #out[i-1,2]
    #out[i-1,2]+left$V2[i]*sin(atan(slope))
    if (inverted){
      deltaLeft <- -left$V1[i] - -left$V1[i-1]
      deltaRight <- -right$V1[i] - -right$V1[i-1]
    } else {
      deltaLeft <- left$V1[i] - left$V1[i-1]
      deltaRight <- right$V1[i] - right$V1[i-1]
    }
    if(inverted){
      out[i, 2] <- out[i-1,2]+(deltaRight*cos(atan(slope)))
      out[i, 3] <- out[i-1,3]+(deltaRight*sin(atan(slope)))
      out[i, 4] <- out[i-1,4]+(deltaLeft*cos(atan(slope)))
      out[i, 5] <- out[i-1,5]+(deltaLeft*sin(atan(slope)))
    } else {
      out[i, 2] <- out[i-1,2]+(deltaLeft*cos(atan(slope)))
      out[i, 3] <- out[i-1,3]+(deltaLeft*sin(atan(slope)))
      out[i, 4] <- out[i-1,4]+(deltaRight*cos(atan(slope)))
      out[i, 5] <- out[i-1,5]+(deltaRight*sin(atan(slope)))
    }
  }
  return(out)
}

drawProfile <- function (coords, centerToFront, centerToBack, clear=TRUE){
  robotPos <- coords[length(coords[,1]),]
  if(identical(robotPos[2]-robotPos[4],0)){
    slope <- 0
  } else{ 
    if(identical(robotPos[3]-robotPos[5],0)){
      slope <- 1
    } else {
      slope <- -1/((robotPos[3]-robotPos[5])/(robotPos[2]-robotPos[4]))
    }
  }
  leftFront <- c(robotPos[2]+(centerToFront*cos(atan(slope))),robotPos[3]+(centerToFront*sin(atan(slope))))
  rightFront <- c(robotPos[4]+(centerToFront*cos(atan(slope))),robotPos[5]+(centerToFront*sin(atan(slope))))
  leftBack <- c(robotPos[2]-(centerToBack*cos(atan(slope))),robotPos[3]-(centerToBack*sin(atan(slope))))
  rightBack <- c(robotPos[4]-(centerToBack*cos(atan(slope))),robotPos[5]-(centerToBack*sin(atan(slope))))
  if (clear){
    plot(coords[,2],coords[,3],type="l", col="Green", ylim=c(-13.5, 13.5),xlim = c(0,54), asp=1)
    field <- read.csv("field.csv")
    #Strings are read as factors by default, so we need to do this to make it read them as strings
    field$col <- as.character(field$col)
    for (i in 1:length(field$x1)){
      lines(c(field$x1[i], field$x2[i]), c(field$y1[i], field$y2[i]), col=field$col[i])
    }
  } else {
    lines (coords[,2],coords[,3],col="Green")
  }
  lines(coords[,4],coords[,5],col="Red")
  lines(c(robotPos[2],leftFront[1]),c(robotPos[3],leftFront[2]), col="Blue")
  lines(c(robotPos[4],rightFront[1]),c(robotPos[5],rightFront[2]), col="Blue")
  lines(c(robotPos[2],leftBack[1]),c(robotPos[3],leftBack[2]), col="Blue")
  lines(c(robotPos[4],rightBack[1]),c(robotPos[5],rightBack[2]), col="Blue")
  lines(c(leftFront[1],rightFront[1]),c(leftFront[2],rightFront[2]),col="Blue")
  lines(c(leftBack[1],rightBack[1]),c(leftBack[2],rightBack[2]),col="Blue")
  endCenter <- c((robotPos[2]+robotPos[4])/2.,(robotPos[3]+robotPos[5])/2.)
}

wheelbaseDiameter <- 26./12.
centerToFront <- (27./2.)/12.
centerToBack <- (27./2.+3.25)/12.
centerToSide <- (29./2.+3.25)/12.
out <- plotProfile(profileName = "Left", inverted = FALSE, wheelbaseDiameter = wheelbaseDiameter, centerToFront = centerToFront,centerToBack =  centerToBack,centerToSide = centerToSide, startY=10.3449-centerToSide)
drawProfile(coords=out, centerToFront=centerToFront, centerToBack=centerToBack)
tmp <- out[length(out[,1]),]
out2 <- plotProfile(profileName = "BlueShoot",inverted = TRUE,wheelbaseDiameter =  wheelbaseDiameter,centerToFront = centerToFront,centerToBack = centerToBack,centerToSide = centerToSide,startPos = tmp)
drawProfile(coords = out2, centerToFront = centerToFront, centerToBack = centerToBack, clear = FALSE)