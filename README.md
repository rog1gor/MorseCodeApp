# MorseCodeApp
Projekt Android 2022

Autorzy: Mateusz Sypniewski, Igor Kamiński, Maria Śmigielska

 ## Kod
 
 Layout: https://github.com/rog1gor/MorseCodeApp/tree/master/app/src/main/res/layout
 
 Kod w javie: https://github.com/rog1gor/MorseCodeApp/tree/master/app/src/main/java/com/example/morsecode
 
 Plik .apk: https://github.com/rog1gor/MorseCodeApp/blob/master/MorseCode.apk

## Lista funkcjonalności
1. Wysyłanie sygnałów alfabetem Morse'a za pomocą latarki, dźwięku lub wibracji. Wysyłanie może być przerwane w trakcie. Długość tekstu nie może przekroczyć 250 znaków, poprawność znaków jest sprawdzana.

    <img src="https://github.com/rog1gor/MorseCodeApp/blob/master/image5.jpg" width="200" height=auto />   <img src="https://github.com/rog1gor/MorseCodeApp/blob/master/image4.jpg" width="200" height=auto />
  
2. Nauka rozpoznawania alfabetu Morse'a (przycisk Nauka!)

   <img src="https://github.com/rog1gor/MorseCodeApp/blob/master/image3.jpg" width="200" height=auto />   <img src="https://github.com/rog1gor/MorseCodeApp/blob/master/image6.jpg" width="200" height=auto />
  
3. Dzielenie tekstu z innych aplikacji z aplikacją MorseCode

   <img src="https://github.com/rog1gor/MorseCodeApp/blob/master/image1.jpg" width="200" height=auto />   <img src="https://github.com/rog1gor/MorseCodeApp/blob/master/image2.jpg" width="200" height=auto />
   
 4. Dwie wersje językowe - polska i angielska, ustawiane automatycznie na podstawie ustawień języka w telefonie. Default wersja to angielski.
 
 ## Dodatkowe uwagi
 1. Wysyłanie wiadomości odbywa się w osobnym wątku. Naciśnięcie przycisku START / STOP oraz przejście do aktywności Nauka przerywają wysyłanie.
 2. Handler wysyłania wiadomości jest przechowywany globalnie.
 
 
 
