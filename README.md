|[![Java CI with Gradle](https://github.com/anaruw/diplom/actions/workflows/workflow_with_mysql_db.yml/badge.svg)](https://github.com/anaruw/diplom/actions/workflows/workflow_with_mysql_db.yml)|[![Java CI with Gradle](https://github.com/anaruw/diplom/actions/workflows/workflow_with_postgresql_db.yml/badge.svg)](https://github.com/anaruw/diplom/actions/workflows/workflow_with_postgresql_db.yml)|
|:---:|:---:|
|MySQL|PostgreSQL|

# Итоговая дипломная работа по курсу "Тестировщик ПО"

Данный проект представляет собой автоматизированное тестирование [целевого приложения](artifacts/aqa-shop.jar).

Приложение использует СУБД (есть поддержка MySQL и PostgreSQL) для хранения информации, а также имеет интерфейс для взаимодействия с банковскими сервисами. [Эмулятор банковских сервисов](artifacts/gate-simulator) написан на Node.js, принимает запросы в необходимом формате и генерирует ответы.

## Начало работы

Для использования на локальном компьютере можно клонировать [данный репозиторий](https://github.com/anaruw/diplom), или скачать zip-архив. 

### Необходимое ПО для запуска

- Git(необязательно: для клонирования репозитория)
- JDK 11
- Docker Desktop
- Google Chrome

### Запуск

1. Скачать репозиторий проекта с помощью команды ```git clone```, или zip-архив
2. Запустить Docker Desktop(убедиться, что Docker Engine запущен)
3. Открыть проект в IDE, или запустить консоль в корневой папке проекта

В консоли

4. ```docker compose up -d```: создание образа эмулятора банковских сервисов, запуск контейнеров СУБД и эмулятора
5. ```java -jar ./artifacts/aqa-shop.jar &```: запуск целевого приложения с подключением к MySQL контейнеру (```java -jar ./artifacts/aqa-shop.jar --spring.datasource.url=jdbc:postgresql://localhost:5432/app &```: для запуска с подключением к PostgreSQL контейнеру)
6. ```./gradlew clean test```: сборка проекта и запуск тестов (```./gradlew clean test -Ddb.url=jdbc:postgresql://localhost:5432/app```: тестирование приложения, использующего PostgreSQL)
7. ```./gradlew allureServe```: формирование отчета и запуск сервера Allure
8. По ссылке с IP-адресом в логах сервера Allure доступен веб-интерфейс Allure Report, где можно ознакомиться с отчетом по результатам проведенного тестирования.

### Примечания

1. Целевое приложение использует только порт 8080, поэтому запустить новый процесс с подключением к другой СУБД возможно после завершения уже запущенного процесса. Завершить процесс можно:
   - сочетанием клавиш *Ctrl+C* в консоли
   - консольной командой
     - ```kill <PID>```: для Linux и MacOS, где *\<PID\>*- номер процесса(указан в логах при запуске *aqa-shop.jar*)
     - ```taskkill /F /PID <PID>```: для Windows, где *\<PID\>*- номер процесса(указан в логах при запуске *aqa-shop.jar*)
    
## Содержимое репозитория

- [Описание](.github/workflows) рабочих процессов для CI/CD GitHub Actions
- Целевое [приложение](artifacts/aqa-shop.jar) для тестирования
- [Эмулятор банковских сервисов](artifacts/gate-simulator)
- [Java-код](src/test/java/ru/netology/diplom) проекта
- [План автоматизации](docs/Plan.md)
