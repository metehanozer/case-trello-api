# trello-api

Trello api test projesi

Testleri çalıştırmak ve raporu görüntülemek için maven ve allure command line kurulu olmalıdır.

Test
~~~
mvn clean -D test=TrelloTest test
mvn clean -D test=SameBoardAndCardTest test
~~~

Test Raporu
~~~
allure serve .\target\allure-results\
~~~