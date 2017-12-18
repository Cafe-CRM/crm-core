INSERT INTO users
(id, first_name, last_name, email, phone, password, shift_salary, salary, bonus, activated, enabled, company_id)
VALUES
  (1, 'Герман', 'Севостьянов', 'boss@mail.ru', '89123456789',
      '$2a$10$Rqc3K45Z8GYbklMvy3640uj/XY4supJ2XmWmS4t1zjBlXiVh3h4aC', 2000, 0, 0, TRUE, TRUE, 1),
  (2, 'Anna', 'Jons', 'manager@mail.ru', '89233456789',
      '$2a$10$OHs.TsEaLmklqTwStaTHLeW3Y/k8fJO5kXklV0nO3ad2b5QbmeVua', 1500, 0, 0, TRUE, TRUE, 1),
  (3, 'Ахмад', 'Чай', 'worker@mail.ru', '89111111111',
      '$2a$10$ffBOimLAaDY5o6jU62oecuKgSgcYAY3FpAS3okZHQw0tHRxV39Oa.', 0, 0, 0, TRUE, TRUE, 1),
  (4, 'Намрег', 'Инверг', 'boss2@mail.ru', '89123456780',
      '$2a$10$ufhLqzo3wbuWKIuQjPnyrOYplalLZsJ8X2NXufOycc/IT2qZKB4m6', 2000, 0, 0, TRUE, TRUE, 2);

insert into users_roles
(user_id, role_id)
values
  (1, 1),
  (2, 2),
  (3, 3),
  (4, 1);

# insert into roles
# (id, name)
# values
#   (1, 'BOSS'),
#   (2, 'MANAGER'),
#   (3, 'WORKER'),
#   (4, 'SUPERVISOR');

Insert into positions
(id, name, percent, use_percent, company_id)
values
  (1, 'Администратор', 5, FALSE, 1),
  (2, 'Владелец', 0, FALSE, 1),
  (3, 'Чайник', 0, FALSE, 1),
  (4, 'Администратор', 0, FALSE, 2),
  (5, 'Владелец', 0, FALSE, 2),
  (6, 'Чайник', 0, FALSE, 2);

insert into users_positions
(user_id, position_id)
values
  (1, 1),
  (2, 2),
  (3, 3),
  (4, 1);

INSERT INTO companies
(id, name)
VALUES
  (1, 'companyA'),
  (2, 'companyB');

insert into ingredients
(id, amount, dimension, name, price, company_id)
values
  (1, 300, 'граммы', 'Сметана', 0.076, 1),
  (2, 3, 'кг', 'Вишня', 278, 1),
  (3, 5, 'кг', 'Зелень', 120.5, 1),
  (4, 15, 'литры', 'Молоко', 69.2, 1),
  (5, 300, 'граммы', 'Сметана', 0.076, 2),
  (6, 3, 'кг', 'Вишня', 278, 2);

insert into category
(id, dirty_profit, floating_price, name, company_id)
values
  (1, TRUE, FALSE, 'Салаты', 1),
  (2, FALSE, FALSE, 'Напитки', 1),
  (3, FALSE, TRUE, 'Десерты', 1),
  (4, TRUE, TRUE, 'Доставка', 1),
  (5, TRUE, FALSE, 'Салаты', 2),
  (6, FALSE, FALSE, 'Напитки', 2),
  (7, FALSE, TRUE, 'Десерты', 2),
  (8, TRUE, TRUE, 'Доставка', 2);

insert into product
(id, cost, description, name, rating, self_cost, company_id)
values
  (1, 400, 'вкусный', 'Цезарь', 0, 200, 1),
  (2, 100, 'вкусный', 'Легкий', 0, 50, 1),
  (3, 100, 'вкусный', 'Летний', 0, 0, 1),
  (4, 15, 'вкусный', 'Кофе', 0, 0, 1),
  (5, 5, 'вкусный', 'Кока-кола', 0, 0, 1),
  (6, 5, 'вкусный', 'Кофе', 0, 0, 1),
  (7, 400, 'вкусный', 'Фруктовый', 0, 0, 1),
  (8, 400, 'вкусный', 'Тирамису', 0, 0, 1),
  (9, 400, 'вкусный', 'Шоколадный', 0, 0, 1),
  (10, 0, 'деревенская пицца', 'Пицца-роял', 0, 0, 1),
  (11, 0, 'филадельфия', 'Суши шоп', 0, 0, 1),
  (12, 0, 'биг мак', 'Макдак', 0, 0, 1),
  (13, 400, 'вкусный', 'Салат1', 0, 200, 2),
  (14, 5, 'вкусный', 'Кока', 0, 0, 2),
  (15, 400, 'вкусный', 'Чизкейк', 0, 0, 2),
  (16, 0, 'деревенская пицца', 'Пицца-роял', 0, 0, 2);

insert into product_and_categories
(product_id, category_id)
values
  (1 , 1),
  (2 , 1),
  (3 , 1),
  (4 , 2),
  (5 , 2),
  (6 , 2),
  (7 , 3),
  (8 , 3),
  (9 , 3),
  (10 , 4),
  (11 , 4),
  (12 , 4),
  (13 , 5),
  (14 , 6),
  (15 , 7),
  (16 , 8);

insert into menu
(id, name, company_id)
values
  (1, 'Обеденное', 1),
  (2, 'Обеденное', 2);

insert into allmenu
(menu_id, category_id)
values
  (1, 1),
  (1, 2),
  (1, 3),
  (1, 4),
  (2, 5),
  (2, 6),
  (2, 7),
  (2, 8);

insert into board
(id, is_open, name, company_id)
values
  (1, TRUE, 'Белый диван', 1),
  (2, TRUE, 'Xbox бочки', 1),
  (3, TRUE, 'Бар', 1),
  (4, TRUE, 'Пожарка', 1),
  (5, TRUE, 'Постер', 1),
  (6, TRUE, 'Белый Xbox', 1),
  (7, TRUE, 'Черный диван', 2),
  (8, TRUE, 'Xbox точки', 2),
  (9, TRUE, 'НеБар', 2);

-- insert into cards (id, name, balance, discount, surname, phone_number, email, advertising, company_id) values (1, 'Данила', 5000, 10, 'Питерский', '82222222222', 'cafe.crm.test@gmail.com', true, 1);
-- insert into cards
-- (id, name, balance, discount, surname, phone_number, advertising, company_id)
-- values
--   (2, 'Кот', 0, 15, 'Барсик', '881111111111', true, 1),
--   (3, 'Пес', 0, 15, 'Барбос', '881111111188', true, 2),
--   (4, 'Пес', 0, 15, 'Барсик', '881111111111', true, 2);

insert into cost_category
(id, name, company_id)
values
  (1, 'Продукты питания', 1),
  (2, 'Продукты питания', 2);

# insert into cost
# (id, name, price, quantity, cost_category_id, date, visible, company_id)
# values
# (1, 'Свинина', 300, 10, 1, curdate());
# (2, 'Сыр', 100, 5, 1, curdate());
# (3, 'Помидоры', 3, 10, 1, curdate());
# (4, 'Хлеб', 30, 10, 1, curdate());
# (5, 'Водка', 400, 1, 1, curdate());
# (6, 'Пиво', 60, 50, 1, curdate());
# (7, 'Чай', 100, 3, 1, curdate());
# (8, 'Кофе', 200, 1, 1, curdate());
# (9, 'Мыло', 90, 3, 1, curdate());
# (10, 'Веревка', 300, 11, 1, curdate());

-- insert into note
-- (id, enable, name, company_id)
-- values
--   (1, true, 'Заметка 1', 1),
--   (2, false, 'Заметка 2', 1),
--   (3, true, 'Заметка 1', 2),
--   (4, false, 'Заметка 2', 2);

insert into property
(id, name, value, company_id)
values
  (1, 'цена за первый час', 300, 1),
  (2, 'Цена за последующие часы', 200, 1),
  (3, 'Реферальный бонус', 150, 1),
  (4, 'masterKey', '$2a$10$FoqlxfGRYkG1pTzMB7STae6G3cclCXZfUXk3Pg1BY21RlVIsvoFxu', 1),
  (7, 'vk', '{"applicationId":"6247124","messageName":"daily-report","chatId":"12","accessToken":"70843606992ba5fb5abbb7621705dcfd44741e9a9e0dab630bed9f9ec126530a0a5a9fea737b2af7af55d","apiVersion":"5.68"}', 1);

insert into mail_settings
(id, email, name_settings, password, smtp_provider, company_id)
values
  (1, 'bond@gmail.com', 'gmail', 'bond', 'smtp.gmail.com', 1);

# insert into trash_property
# (id, name, property, company_id)
# values
#   (1, 'vk', '{"applicationId":"","messageName":"daily-report","chatId":"","accessToken":"","apiVersion":"5.68"}', 1),
#   (2, 'vk', '{"applicationId":"","messageName":"daily-report","chatId":"","accessToken":"","apiVersion":"5.68"}', 2);

# insert into template
# (id, content, name)
# values
#   (1, '<!DOCTYPE html>
# <html xmlns:th="http://www.thymeleaf.org" lang="en" xmlns="http://www.w3.org/1999/html">
# <head>
#     <meta charset="UTF-8"/>
#     <title>Title</title>
# </head>
# <body>
# <p>Реклама была успешно отключена.</p>
# <p>В случае если в дальнешем захотите получать рекламную рассылку, то передите по этой ссылке :</p>
# <a th:href="''http://'' + ${siteAddress} + ''/advertising/toggle?number='' + ${number} + ''&amp;token='' + ${token}">Если
#     больше не хотите получать рекламу, нажмите на это сообщение</a>
# </body>
# </html>', 'disable-advertising'),
#
#   (2, '<!DOCTYPE html>
# <html xmlns:th="http://www.thymeleaf.org" lang="en" xmlns="http://www.w3.org/1999/html">
# <head>
#     <meta charset="UTF-8"/>
#     <title>Title</title>
# </head>
# <body>
# <div th:utext="${advertisingText}" th:remove="tag"></div>
# <br/>
# <a th:href="''http://'' + ${siteAddress} + ''/advertising/toggle?number='' + ${number} + ''&amp;token='' + ${token}">Если
#     больше не хотите получать рекламу, нажмите на это сообщение</a>
# </body>
# </html>', 'text-advertising'),
#
#   (3, '<!DOCTYPE html>
# <html xmlns:th="http://www.thymeleaf.org" lang="en" xmlns="http://www.w3.org/1999/html">
# <head>
# </head>
# <body>
# <table align="center" border="0" cellpacing="0" cellpadding="0" width="550" style="background-color: #88ffee">
#     <tbody>
#     <tr>
#         <td>
#             <table>
#                 <tbody>
#                 <tr>
#                     <td width="450">
#                         <img src="https://pp.userapi.com/c639220/v639220279/eb29/bGMV4tU4pDU.jpg" alt="PACMAN"
#                              border="0"
#                              width="50" height="50"/>
#                     </td>
#                     <td style="border-left:#e7e7e7 1px solid;text-align:center;vertical-align:middle;"
#                         valign="middle">
#                         <a href="https://vk.com/hookahpacman" alt="Вконтакте">
#                             <img src="https://lh3.googleusercontent.com/BIiGrMnIkSZXn3a8qYmEbKShNutHhqK7AEcWaLOlyZ9vxmf5QXPPoeUFpsEhH42fww=w300"
#                                  alt="VK" border="0" width="45" height="45"/>
#                         </a>
#                     </td>
#                     <td style="border-left:#e7e7e7 1px solid;text-align:center;vertical-align:middle;"
#                         valign="middle">
#                         <a href="https://www.instagram.com/hookahpacman/" alt="Инстаграм">
#                             <img src="https://lh3.googleusercontent.com/aYbdIM1abwyVSUZLDKoE0CDZGRhlkpsaPOg9tNnBktUQYsXflwknnOn2Ge1Yr7rImGk=w300"
#                                  alt="VK" border="0" width="40" height="40"/>
#                         </a>
#                     </td>
#                 </tr>
#
#                 </tbody>
#             </table>
#         </td>
#     </tr>
#     <tr>
#         <td>
#             <table>
#                 <tbody>
#                 <tr>
#                     <td>
#                         <a href="https://vk.com/hookahpacman" th:href="${urlToLink}">
#                             <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRa-dkyRnlAJVrYcHi6qneUAxUQPF4f_JeXYAtX4pToMygPkbxbCQ"
#                                  th:src="${advertisingUrl}" width="550"/>
#                         </a>
#                     </td>
#                 </tr>
#                 </tbody>
#             </table>
#         </td>
#     </tr>
#     <tr>
#         <td>
#             <table>
#                 <tbody>
#                 <tr>
#                     <td>
#                         <a th:href="''http://'' + ${siteAddress} + ''/advertising/toggle?number='' + ${number} + ''&amp;token='' + ${token}">Если
#                             больше не хотите получать рекламу, нажмите на это сообщение</a>
#                     </td>
#                 </tr>
#                 </tbody>
#             </table>
#         </td>
#     </tr>
#     </tbody>
#
# </table>
# </body>
# </html>', 'image-advertising'),
#
#   (4, '<!DOCTYPE html>
# <html xmlns:th="http://www.thymeleaf.org" lang="en" xmlns="http://www.w3.org/1999/html">
# <head>
#     <meta charset="UTF-8"/>
#     <title>Title</title>
# </head>
# <body>
# <h2 th:text="''Здравствуйте, с баланса вашей карты было списано : '' + ${deductionAmount} + '' руб.''"></h2>
# <h2 th:text="''Текущий баланс карты : '' + ${newBalance} + '' руб.''"></h2>
# </body>
# </html>', 'balance-info-deduction'),
#
#   (5, '<!DOCTYPE html>
# <html xmlns:th="http://www.thymeleaf.org" lang="en" xmlns="http://www.w3.org/1999/html">
# <head>
#     <meta charset="UTF-8"/>
#     <title>Title</title>
# </head>
# <body>
# <h2 th:text="''Здравствуйте, на баланс вашей карты было начислено : '' + ${refillAmount} + '' руб.''"></h2>
# <h2 th:text="''Текущий баланс карты : '' + ${newBalance} + '' руб.''"></h2>
# </body>
# </html>', 'balance-info-refill'),
#
#   (6, '<!DOCTYPE html>
# <html lang="en">
# <head>
#     <meta charset="UTF-8"/>
#     <title>Title</title>
# </head>
# <body>
# <div>
#     <h1>Токен доступа недействителен!</h1>
# </div>
# </body>
# </html>', 'invalid-token'),
#
#   (7, '<!DOCTYPE html>
# <html xmlns:th="http://www.thymeleaf.org" lang="en" xmlns="http://www.w3.org/1999/html">
# <head>
#     <meta charset="UTF-8"/>
#     <title>Title</title>
# </head>
# <body>
# <div th:each="user:${recipients}">
#     <h2>Добрый день <span th:text="${user.firstName+'' ''+user.lastName}"></span>
#     </h2>
# </div>
# <h2 th:text="${message}"></h2>
#
# <h2>Сотрудники на смене:
#     <br></br>
#     <div th:each="user :${usersOnShift}">
#         <br></br>
#         <span th:text="${user.firstName+'' ''+user.lastName+'':''+user.positions}"></span>
#     </div>
# </h2>
# <h2>Количество клиентов за смену: <span th:text="${clients}"></span></h2>
# <h2>Количество счетов за смену: <span th:text="${calculate}"></span></h2>
# <h2>Прибыль за смену: <span th:text="${allPrice + '' '' + ''рублей''}"></span></h2>
# <h2>Касса : <span th:text="${cashBox + '' '' + ''рублей''}"></span></h2>
# <h2>Недосдача: <span th:text="${shortage + '' '' + ''рублей''}"></span></h2>
# </body>
# </html>', 'closeShiftEmailShortage'),
#
#   (8, '{0}
# {1} {2}
# Грязными: {3}
#
# Количество гостей:
# {4}
# Всего: {5}
#
# Зарплата Сотрудников:
# {6}
# {7}
#
# Всего расходов за день - {8}
#
# Наличными - {9}
# Карта - {10}
# Общая Сумма - {11}
# {12}
# {13}', 'daily-report');