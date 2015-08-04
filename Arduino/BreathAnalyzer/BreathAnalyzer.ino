#include <Timer.h>
#include <SoftwareSerial.h>

SoftwareSerial softwareSerial(7,8); 

Timer timer(2000);
Timer counter(1000);

void setup() {
  Serial.begin(9600);
  softwareSerial.begin(9600);
  timer.setCallBack(callBack);
  counter.setCallBack(callBackCounter);
}


void callBackTimer() {
  Serial.println("-");
}

void callBackCounter() {
  static int count = 0;
  Serial.println(count);
  count++;
}

void loop() {
  timer.run();
  counter.run();
  
  if (softwareSerial.available()) {
    Serial.print(softwareSerial.read());
  }
}
