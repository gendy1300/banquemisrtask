# banquemisrtask
[![LinkedIn][linkedin-shield]][linkedin-url]



<!-- PROJECT LOGO -->
<br />
<div align="center">

  <h3 align="center">Currency Converter</h3>




<!-- ABOUT THE PROJECT -->
## About The Project


The Currency Converter app converts from 170 currencies in real time and can access the currency's historical data for the past three days.



### Built Using

This project's design pattern was MVVM.

The app uses [fixer](https://fixer.io/) apis to fetch currencies 

This is a list of all technologies used in the project:

Kotlin.
Data binding.
Navigation component.
Dagger-Hilt.
Coroutines.
Retrofit.



## Unit Testing

The project has some unit and instrumented tests that you can find in [StarterTest.kt](https://github.com/gendy1300/banquemisrtask/blob/master/app/src/androidTest/java/com/ahmedelgendy/banquemisrtask/StarterTest.kt) it will test navigation and if 
the fields will accept numbers only or not.

Also will test the changes in text fields to see if they will affect the other field or not, and you will find these unit tests here [ConvertTest.kt](https://github.com/gendy1300/banquemisrtask/blob/master/app/src/androidTest/java/com/ahmedelgendy/banquemisrtask/ConvertTest.kt)


<!-- USAGE EXAMPLES -->
## Usage

After launching, the app will display a screen with input and output fields and two currency pickers. After choosing a currency, the output will be converted accordingly.

After typing in the input/output field, the user will be given 1 second before calling the convert API, and the value of the other field changes accordingly.

After pressing the "Details" button, a new screen with three sections will appear. The first section will display historical data for your from and to selections for the last three days.
 In the second section, a chart will represent the historical data for your from and to selections for the last three days.

Finally, in the last section, your from currency will be converted to 10 other popular currencies.




<!-- CONTACT -->
## Contact

Ahmed Jamal Elgendy 

Email : El-gendy@outlook.com

Phone number: 1032604187








<!-- MARKDOWN LINKS & IMAGES -->

[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/a-gendy/
