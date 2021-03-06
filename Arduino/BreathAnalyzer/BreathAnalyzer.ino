#include <SerialData.h>
#include <SoftwareSerial.h>

SoftwareSerial softwareSerial(7,8); 

int vccMilliVolt, sensorMilliVolt, sensorValue;

void setup() {
  Serial.begin(9600);
  softwareSerial.begin(9600); 
}


void loop() {
  //serialData.run();
  vccMilliVolt = readVcc();
  sensorValue = analogRead(A0);
  sensorMilliVolt = sensorValue * (vccMilliVolt / 1023.0);
  
  
  softwareSerial.print("{\"Device\":{\"");
  softwareSerial.print("analogValue");
  softwareSerial.print("\":\"");
  softwareSerial.print(sensorMilliVolt);
  softwareSerial.print("\",");
  softwareSerial.print("workingVoltage");
  softwareSerial.print("\":\"");
  softwareSerial.print(vccMilliVolt);
  softwareSerial.print("\"}}");
  softwareSerial.print("\r\n");
  
//  Serial.print("{\"Device\":{\"");
//  Serial.print("analogValue");
//  Serial.print("\":\"");
//  Serial.print(sensorMilliVolt);
//  Serial.print("\",");
//  Serial.print("workingVoltage");
//  Serial.print("\":\"");
//  Serial.print(vccMilliVolt);
//  Serial.print("\"}}");
//  Serial.print("\r\n");
  
  //if (softwareSerial.available()) {
  //  Serial.print(softwareSerial.read());
  //}
}


long readVcc() {
  // Read 1.1V reference against AVcc
  // set the reference to Vcc and the measurement to the internal 1.1V reference
  #if defined(__AVR_ATmega32U4__) || defined(__AVR_ATmega1280__) || defined(__AVR_ATmega2560__)
    ADMUX = _BV(REFS0) | _BV(MUX4) | _BV(MUX3) | _BV(MUX2) | _BV(MUX1);
  #elif defined (__AVR_ATtiny24__) || defined(__AVR_ATtiny44__) || defined(__AVR_ATtiny84__)
    ADMUX = _BV(MUX5) | _BV(MUX0);
  #elif defined (__AVR_ATtiny25__) || defined(__AVR_ATtiny45__) || defined(__AVR_ATtiny85__)
    ADMUX = _BV(MUX3) | _BV(MUX2);
  #else
    ADMUX = _BV(REFS0) | _BV(MUX3) | _BV(MUX2) | _BV(MUX1);
  #endif  
 
  delay(2); // Wait for Vref to settle
  ADCSRA |= _BV(ADSC); // Start conversion
  while (bit_is_set(ADCSRA,ADSC)); // measuring
 
  uint8_t low  = ADCL; // must read ADCL first - it then locks ADCH  
  uint8_t high = ADCH; // unlocks both
 
  long result = (high<<8) | low;
 
  result = 1125300L / result; // Calculate Vcc (in mV); 1125300 = 1.1*1023*1000
  return result; // Vcc in millivolts
}
