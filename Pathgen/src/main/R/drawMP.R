left <- read.csv("../../../../calciferLeftLeftProfile.csv", header=FALSE)
right <- read.csv("../../../../calciferRightLeftProfile.csv", header=FALSE)
wheelbaseDiameter <- 26./12.
centerToFront <- (27./2.)/12.
centerToBack <- (27./2.+3.25)/12.
centerToSide <- (29./2.+3.25)/12.
startingCenter <- c(91./12.-centerToSide,centerToBack)
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
out <- array(dim=c(length(left$V1)+1,5))
out[1,]<-c(0, startingCenter[2], (startingCenter[1]+wheelbaseDiameter/2.), startingCenter[2], (startingCenter[1]-wheelbaseDiameter/2.))
#out[1,]<-c(0,0,0,0,0)
forward <- 1
for(i in 2:length(left$V4)){
  if(isTRUE(all.equal(out[i-1,2]-out[i-1,4],0))){
    slope <- 0
    forward <- -forward
  } else{ 
    if(isTRUE(all.equal(out[i-1,3]-out[i-1,5],0))){
      slope <- forward
    } else {
      slope <- -1/((out[i-1,3]-out[i-1,5])/(out[i-1,2]-out[i-1,4]))
    }
  }
  out[i,1] <- left$V4[i]
  #left$V2[i]
  #out[i-1,2]
  #out[i-1,2]+left$V2[i]*sin(atan(slope))
  deltaLeft <- left$V1[i] - left$V1[i-1]
  deltaRight <- right$V1[i] - right$V1[i-1]
  out[i, 2] <- out[i-1,2]+(deltaLeft*cos(atan(slope)))
  out[i, 3] <- out[i-1,3]+(deltaLeft*sin(atan(slope)))
  out[i, 4] <- out[i-1,4]+(deltaRight*cos(atan(slope)))
  out[i, 5] <- out[i-1,5]+(deltaRight*sin(atan(slope)))
}
leftFront <- c(out[length(left$V4),2]+(centerToFront*cos(atan(slope))),out[length(left$V4),3]+(centerToFront*sin(atan(slope))))
rightFront <- c(out[length(left$V4),4]+(centerToFront*cos(atan(slope))),out[length(left$V4),5]+(centerToFront*sin(atan(slope))))
plot(out[,2],out[,3],type="l", col="Green", ylim=c(-13.5, 13.5),xlim = c(0,54), asp=1)
lines(out[,4],out[,5],col="Red")
lines(c(out[length(left$V4),2],leftFront[1]),c(out[length(left$V4),3],leftFront[2]), col="Blue")
lines(c(out[length(left$V4),4],rightFront[1]),c(out[length(left$V4),5],rightFront[2]), col="Blue")
endCenter <- c((out[length(out[,2]),2]+out[length(out[,4]),4])/2.,(out[length(out[,3]),3]+out[length(out[,5]),5])/2.)
field <- read.csv("field.csv")
for (i in 1:length(field$x1)){
  lines(c(field$x1[i], field$x2[i]), c(field$y1[i], field$y2[i]), col=field$col[i])
}
