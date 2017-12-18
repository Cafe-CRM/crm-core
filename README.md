# Package and run jar

For build jar you must enter to root project folder and execute command:<br />
mvn package<br />
For run jar under windows in same folder,execute command:<br />
java -Dfile.encoding=UTF-8 -jar target/crm-core.jar


**Расчет денежный средств**<br />
формула грязных = Сумма всего времени (с учётом скидок) + сумма всего меню, которое должно идти в грязные + все приходы за смену - все долги за смену
<br /><br />
Касса = сумма денег на карте и налик за прошлую смену + сумма грязных сегодня - все расходы (не зп) сегодня - (зп (оклад) всех сотрудников + все % всех сотрудников, как % от продажи еды/напитков, так и от грязных)