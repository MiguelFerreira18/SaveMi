# SaveMi (Save My Income)

A self financial management tool, allows the user to manage their financial life, from managing their income, payments, wishes, loans to also incorporate investments calulated through an stock exchange api.

## Motive

In this current day and age where economic uncertainty worries our minds, there is an increased importance in carefully managing our financial situation. After learning a bit of financial literacy, it made me understand the importance of good money management, giving me an opportunity to develop this software application. Another big factor was the security of self hosting the app, not allowing a third party to store financial data.

## What the tool won't do

Syncronize data with banks

## Tooling

#### CI/CD

- Docker (Only the default Springboot docker package)
- Jenkins (Not started yet)

#### Front-End

- Angular 19
- Angular Material UI
- Tailwind 4

#### Back-End

- Springboot 3.5.4
- Mysql
- Flyway

#### Testing

- Jasmine and Karma (Angular 19 defaults)
- Junit

## Features (Work in Progress)

## How to run the project

1. Clone it to your computer
2. Have java 21 installed, either compile the java project into a jar with ```-DskipTests``` or just run the project on intelij (recommended)
3. Run the dev profile on the springboot api
4. Let the api create the docker container of mysql and insert all the bootstrap data
5. Run ```bash ng serve``` inside publicA directory where the frontend is located
6. Happy coding

**Note:** The login credentials can be found at the bootsrap java files in ```src/main/java/com/money/SaveMi/init/UserBootstrap.java```

## How to run for production

1. Create the secret keys using *setup-keys.sh* in linux or *setup-keys.bat* for windows
2. Run ```Docker compose -f Docker-Compose.prod.yml up```

## Sources of Knowledge (Work in Progress)

### Videos

- [Financial Literacy - Full Video](https://www.youtube.com/watch?v=4j2emMn7UaI)
- [Master Financial Literacy in 54 Minutes: Everything They Never Taught You About Money!](https://youtu.be/vJabNEwZIuc?si=qglwYJIqop8EZOU4)
- [Financial Literacy In 63 Minutes](https://youtu.be/ouvbeb2wSGA?si=l0tXN3_TJvcuZ_Fp)

(If you are Portuguese)

- [Curso de Literacia Financeira](https://youtube.com/playlist?list=PLoH8sKr6_cvCVGUmNOUaJYMgsx4LEAV69&si=kY_UkHKfOUNbzlK5)
- [Literacia Financeira em Portugal](https://youtube.com/playlist?list=PLapXKb-WbLIUpGlAiNk1jfQdJMhDpfS5a&si=k9glPJe0fEzP_LAl)

### Books
