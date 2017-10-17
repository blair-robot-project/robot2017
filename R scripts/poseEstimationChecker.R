rad2deg <- function(rad) {(rad * 180) / (pi)}
deg2rad <- function(deg) {(deg * pi) / (180)}

calcWheelbase <- function(left, right, angle){
  return((left-right)/angle);
}

smoothValue <- function(value, timeMillis, n){
  smoothed <- (value[(n+1):length(value)] - value[1:(length(value)-n)])/(timeMillis[(n+1):length(timeMillis)] - timeMillis[1:(length(timeMillis)-n)])*1000;
  return(c(rep(0, floor(n/2)), smoothed, rep(0, ceiling(n/2))));
}

plotWheelVsVel <- function(leftPos, rightPos, rawAngleDegrees, timeMillis, angularVelThreshRad, smoothingConst){
  #Smooth values
  angular <- deg2rad(smoothValue(rawAngleDegrees, timeMillis, smoothingConst))
  left <- smoothValue(leftPos, timeMillis, smoothingConst)
  right <- smoothValue(rightPos, timeMillis, smoothingConst)
  
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

  
  return(angular)
}