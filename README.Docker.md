### Building and running your application

When you're ready, start your application by running:
`docker compose up --build`.

Your application will be available at http://localhost:80.

### Deploying your application to the cloud

First, build your image, e.g.: `docker build -t myapp .`.
If your cloud uses a different CPU architecture than your development
machine (e.g., you are on a Mac M1 and your cloud provider is amd64),
you'll want to build the image for that platform, e.g.:
`docker build --platform=linux/amd64 -t myapp .`.

Then, push it to your registry, e.g. `docker push myregistry.com/myapp`.

Consult Docker's [getting started](https://docs.docker.com/go/get-started-sharing/)
docs for more detail on building and pushing.

--------------------------------------------------------------------------------

### Создание и запуск вашего приложения

Когда вы будете готовы, запустите приложение, выполнив команду:
`docker compose up -build`.

Ваше приложение будет доступно по адресу http://localhost:80.

### Развертывание вашего приложения в облаке

Сначала создайте свой образ, например: `docker build -t myapp .`.
Если в вашем облаке используется архитектура процессора, отличная от архитектуры вашей
машины разработки (например, вы используете Mac M1, а ваш облачный провайдер - amd64),
вам нужно создать образ для этой платформы, например:
`docker build --platform=linux/amd64 -t myapp .`.

Затем перенесите его в свой реестр, например, "docker push myregistry.com/myapp`.

Обратитесь к документации Docker [начало работы](https://docs.docker.com/go/get-started-sharing/)
для получения более подробной информации о сборке и переносе.