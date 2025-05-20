|[![Java CI with Gradle](https://github.com/anaruw/diplom/actions/workflows/gradle.yml/badge.svg?branch=main)](https://github.com/anaruw/diplom/actions/workflows/gradle.yml) |[![Java CI with Gradle](https://github.com/anaruw/diplom/actions/workflows/gradle.yml/badge.svg?branch=postgresql)](https://github.com/anaruw/diplom/actions/workflows/gradle.yml)|
|:---:|:---:|
|MySQL|PostgreSQL|

# Порядок действий

1. Запустить Docker Desktop(или иное ПО, запускающее Docker Engine)
   
Далее приведены консольные команды, работающие из консоли, открытой в корневой папке проекта(можно пользоваться консолью IDE, предварительно открыв проект)

2. ```docker compose up -d``` Запуск контейнеров с СУБД и *gate-simulator* (флаг -d позволяет продолжить работу в этом экземпляре консоли)
3. ```java -jar ./artifacts/aqa-shop.jar &``` Запуск тестируемого приложения(& позволяет продолжить работу в этом экземпляре консоли)
4. ```./gradlew clean test``` Сборка проекта и прохождение тестов
5. ```./gradlew allureServe``` Формирование и запуск Allure- отчета

   а) пройти по ссылке с IP-адресом, чтобы посмотреть отчет
7. Завершить работу сервера Allure (сочетанием клавиш *Ctrl+C*, или иным способом, указанном в подсказке)
8. Завершить работу *aqa-shop.jar*. Варианты:

   а) сочетанием клавиш *Ctrl+C*

   б) ```kill <PID>```(для Linux, MacOS; вместо <PID> подставить идентификатор процесса)

   в) ```taskkill /pid <PID>``` (для Windows, вместо <PID> подставить идентификатор процесса)

   г) ```jps``` Поможет найти нужный идентификатор процесса(есть и другие варианты)
10. ```git log --oneline``` Узнать, в какой ветке<sup>*</sup> проекта находимся(указатель *HEAD*)
11. ```git switch postgresql``` Переключиться на ветку<sup>*</sup> *postgresql*(если в п.6 мы находились в этой ветке, заменить *postgresql* на *main*)
12. Повторить п.п. 3-7 для второй ветки проекта
13. ```docker compose down``` Завершить работу контейнеров

<sup>*</sup>- ветка *main* настроена на работу с MySQL, *postgresql*- с PostgreSQL
