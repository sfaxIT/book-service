INSERT INTO Author (author_id, author_username, author_password, author_pseudonym, author_role)
VALUES ('1', 'Saber', 'wookie1', 'wookie1', 'ADMIN');
INSERT INTO Author (author_id, author_username, author_password, author_pseudonym, author_role)
VALUES ('2', 'Mahdi', 'wookie2', 'wookie2', 'AUTHOR');
INSERT INTO Book (book_id, book_title, book_description, book_image, book_price, author_id)
VALUES ('X1', 'SaberBook1', 'Saber first book',null, 19.99, '1');
INSERT INTO Book (book_id, book_title, book_description, book_image, book_price, author_id)
VALUES ('X2', 'SaberBook2', 'Saber second book',null, 39.99, '1');
INSERT INTO Book (book_id, book_title, book_description, book_image, book_price, author_id)
VALUES ('X3', 'MahdiBook1', 'Mahdi first book',null, 9.99, '2');