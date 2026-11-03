# GameFlix Login and Registration

This project uses Java 21, Spring Boot 4.0.8, and MySQL.
It lets users register, log in, and view their own profile
using a JWT.

## Setup

You need Java 21 and a running MySQL server.

Sign in to MySQL as an administrator and create the
database and application user:

```sql
CREATE DATABASE gameflix;

CREATE USER 'gameflix_app'@'localhost'
IDENTIFIED BY 'CHOOSE_YOUR_PASSWORD';

GRANT ALL PRIVILEGES ON gameflix.*
TO 'gameflix_app'@'localhost';
```

The app connects to the gameflix database on localhost:3306
using the gameflix_app account.

Create application-local.properties inside
src/main/resources and add:

```properties
spring.datasource.password=YOUR_DATABASE_PASSWORD
app.jwt.secret=YOUR_BASE64_SECRET
```

To generate the secret, run:

```bash
openssl rand -base64 32
```

Paste the generated value without quotation marks.
application-local.properties is in .gitignore because
it contains private values. An application.properties.example
file is included to show the configuration keys.

From the project folder, run:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

The app runs at http://localhost:8080.

## Endpoints

| Request | Result |
|---|---|
| POST /api/auth/register | 201 when a user is created |
| POST /api/auth/login | 200 with a JWT when credentials are correct |
| GET /api/users/me | 200 with the user's profile when the token is valid |

Registration takes a username, email, and password.
Login takes an email and password.

Invalid registration input returns 400. Registering an
existing email returns 409. Incorrect login credentials
return 401 with the same generic error regardless of
whether the email or password was wrong.

For /api/users/me, send the token in the Authorization
header as Bearer YOUR_TOKEN. Missing, invalid, or expired
tokens return 401. Profile responses do not include
passwords or hashes.

## Database schema

This is the users table created in MySQL:

```sql
CREATE TABLE users (
  id int NOT NULL AUTO_INCREMENT,
  created_at datetime(6) NOT NULL,
  email varchar(254) NOT NULL,
  password varchar(255) NOT NULL,
  username varchar(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;
```

The unique constraint prevents duplicate emails at the
database level. Emails are converted to lowercase before
they are saved.

## Password storage

I used bcrypt with a work factor of 12. This is above
the assignment's stated minimum of 10 and makes password
guessing more expensive while still working well for
this project. The delegating encoder adds {bcrypt} to
identify the algorithm and allow future changes.

Passwords must be 8–64 printable ASCII characters so
they stay below bcrypt's 72-byte limit. The password
column is VARCHAR(255), and only the hash is stored.

## Keeping data between runs

I used spring.jpa.hibernate.ddl-auto=update so Hibernate
can set up the table without deleting registered users
each time the app restarts. This also lets me reuse the
test account during testing.

## JWT and security

Tokens expire after 15 minutes. I chose this to give
users time to use the app while limiting how long a
stolen token would remain usable.

Tokens use HS256 signing. The secret comes from the
untracked local configuration file. The server checks
the signature, expiration, issuer, and required claims
before accepting the token.

The token contains the user ID, issuer, issued-at time,
and expiration. Its payload is readable because JWTs
are encoded, not encrypted, so no password or hash is
included.

CORS allows only http://localhost:3000. The API uses
bearer tokens rather than cookie-based login, so it
is stateless and CSRF protection is disabled.

## Testing

I tested the service with Postman. The Word document
includes screenshots of:

1. Successful registration — 201.
2. Duplicate email rejection — 409.
3. Successful login with a token — 200.
4. Viewing the profile with a valid token — 200.
5. Requesting the profile without a token — 401.
6. Logging in with a wrong password — 401.
7. The MySQL query showing the stored password hash.

The Postman collection is included with the project.
Use a new email to repeat successful registration.
An existing email should return 409. Log in again
when a token expires.

## Changes from Module 3

My ERD used User with user_id and password_hash.
The implementation uses users, id, and password to
match the assignment's database query. The Java field
is still called passwordHash.

I also added specific column lengths, an auto-generated
ID, and a unique email constraint. The other ERD tables
are outside the scope of this login service.

## AI use

I used ChatGPT to help with setup, code, troubleshooting,
and the README. I reviewed the password hashing, token
checks, and handling of private configuration values.

I changed the AI-generated login error handling to return
an explicit JSON message instead of relying on Spring's
default error response. This keeps the response consistent
and avoids revealing whether the email or password was wrong.

