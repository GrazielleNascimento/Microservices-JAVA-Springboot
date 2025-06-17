# 🐾 Sistema de Gerenciamento Pet Care - Arquitetura Microsserviços

## 📌 Descrição
Sistema completo para gerenciamento de pets, composto por múltiplos microsserviços que trabalham de forma integrada. A plataforma permite o cadastro de pets, agendamento automático e manual de cuidados, e notificações por e-mail para os tutores, utilizando comunicação assíncrona para garantir uma arquitetura resiliente e escalável.

## 🚀 Funcionalidades Principais

### 1️⃣ Microsserviço de Cadastro de Pets (PetsGFT)
- 📝 **Cadastro Completo**: Nome, espécie (cachorro ou gato), tutor, e-mail, raça, idade, peso, cor e descrição.
- 🖼️ **Integração com APIs Externas**: TheCatAPI e TheDogAPI para enriquecimento dos perfis com imagens.
- 🔍 **Busca Avançada**: Filtragem por espécie, raça, tutor e outras características.
- 📡 **Comunicação Assíncrona**: Publica eventos no RabbitMQ quando novos pets são cadastrados.

### 2️⃣ Microsserviço de Agendamento (MsCareScheduling)
- 🔄 **Agendamentos Automáticos**: Cria agendamentos iniciais baseados na idade e características do pet:
  - Primeira vacina para filhotes
  - Banho grátis para todos os pets novos
  - Check-up inicial para pets com mais de 6 meses
  - Consultas semestrais para pets idosos (7+ anos)
- 📅 **Agendamentos Manuais**: Permite criar agendamentos personalizados conforme necessidades específicas.
- 🔄 **Integração com Cadastro**: Solicita informações do pet através de comunicação assíncrona via RabbitMQ.

### 3️⃣ Microsserviço de Notificações (MsNotificationEmail)
- 📧 **E-mails Automáticos**: Envia confirmações de agendamentos para os tutores.
- 🔄 **Processamento Assíncrono**: Escuta eventos no RabbitMQ para novos agendamentos.
- 🔁 **Mecanismo de Retry**: Implementa tentativas de reenvio em caso de falhas.

### 4️⃣ Serviços de Suporte
- 🔄 **Gateway (MsCloudGateway)**: Centraliza o acesso às APIs com porta fixa.
- 🔍 **Discovery Server (EurekaServer)**: Registro e descoberta de serviços.
- 🐶 **DogsGFT e CatsGFT**: APIs específicas para integração com TheDogAPI e TheCatAPI.

## 🛠️ Tecnologias Utilizadas
- **Java**: 21
- **Spring Boot**: 3.1.0
- **Spring Cloud**: Netflix Eureka, Gateway
- **Maven**: 3.8.6
- **MySQL**: 8.0
- **RabbitMQ**: 3.9
- **MailHog**: Para testes de e-mail
- **Docker**: 20.10
- **Docker Compose**: 1.29

## 📊 Arquitetura do Sistema

```
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│                 │      │                 │      │                 │
│  PetsGFT        │◄────►│  DogsGFT/CatsGFT│      │ MsNotification  │
│  (Cadastro)     │      │  (API Externa)  │      │  (E-mails)      │
│                 │      │                 │      │                 │
└───────┬─────────┘      └─────────────────┘      └────────▲────────┘
        │                                                   │
        │                                                   │
        │                                                   │
        │                ┌─────────────────┐               │
        │                │                 │               │
        └───────────────►│   RabbitMQ      │───────────────┘
                         │   (Mensageria)  │               ▲
                         │                 │               │
                         └────────┬────────┘               │
                                  │                        │
                                  │                        │
                                  ▼                        │
                         ┌─────────────────┐               │
                         │                 │               │
                         │ MsScheduling    │───────────────┘
                         │ (Agendamentos)  │
                         │                 │
                         └─────────────────┘
```

![img_2.png](img_2.png)

## ▶️ Como Executar

### 1️⃣ Pré-requisitos
- Docker e Docker Compose instalados.
- Git para clonar o repositório.

### 2️⃣ Passos para execução

```sh
# Clone o repositório
git clone <URL_DO_REPOSITORIO>
cd <NOME_DO_REPOSITORIO>

# Inicie os serviços de infraestrutura com Docker Compose
docker-compose up -d

# Verifique se todos os serviços estão em execução
docker ps

# Aguarde os containers iniciarem completamente antes de iniciar os microsserviços
```

Abaixo está o Docker Compose para iniciar os serviços de infraestrutura:

```yaml
version: '3.8'

services:
  mailhog:
    image: mailhog/mailhog
    container_name: mailhog
    ports:
      - "1025:1025"
      - "8025:8025"

  rabbitmq:
    image: rabbitmq:3-management
    container_name: rabbitmq
    ports:
      - "5673:5672"
      - "15673:15672"
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest

  mysql:
    image: mysql:5.7
    container_name: mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: db_pets
    ports:
      - "3306:3306"
    volumes:
      - db_data:/var/lib/mysql
    command: --default-authentication-plugin=mysql_native_password

volumes:
  db_data:
```

### 3️⃣ Ordem de inicialização dos microsserviços:
```sh
# Iniciar primeiro a infraestrutura
docker-compose up -d

# Em seguida, iniciar os microsserviços na seguinte ordem:
1. EurekaServer (Service Discovery)
2. DogsGFT
3. CatsGFT
4. PetsGFT
5. MsCareScheduling (Appointment)
6. MsNotificationEmail
7. MsCloudGateway
```

![img.png](img.png)

![img_1.png](img_1.png)

### 4️⃣ Acesso à aplicação:
- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761 
  - Ao acessar, será solicitado login. Use:
  - Usuário: `admin`
  - Senha: `admin`
- **RabbitMQ Management**: http://localhost:15673 (user: guest, password: guest)
- **MailHog (teste de e-mails)**: http://localhost:8025

## 🧪 Testando a Aplicação

### 🔗 Principais Endpoints

#### 📋 Microsserviço de Pets (através do Gateway)
- `GET /api/pets/status` → Verifica status do serviço
- `POST /api/pets` → Cadastra novo pet
- `GET /api/pets/{id}` → Busca pet por ID
- `GET /api/pets/search` → Busca com filtros
- `PUT /api/pets/{id}` → Atualiza pet
- `DELETE /api/pets/{id}` → Remove pet

#### 📅 Microsserviço de Agendamentos (através do Gateway)
- `POST /api/appointments` → Cria agendamento manual
- `GET /api/appointments` → Lista todos agendamentos

### 📝 Exemplo de Payload para Cadastro de Pet
```json
{
  "name": "Rex",
  "species": "DOG",
  "breed": "Labrador",
  "birthDate": "2023-01-15",
  "weight": 30.5,
  "color": "Yellow",
  "description": "Friendly dog",
  "tutor": {
    "name": "John Doe",
    "email": "john.doe@example.com"
  }
}
```

### 📝 Exemplo de Payload para Agendamento Manual
```json
{
  "petId": 1,
  "appointmentType": "BATH_AND_GROOMING",
  "appointmentDate": "2025-03-25T14:30:00",
  "description": "Banho mensal com tosa higiênica"
}
```

## 🌐 Estratégia de Portas e Escalabilidade

### 🔍 Eureka Service Discovery e Portas Dinâmicas
O sistema utiliza o Netflix Eureka como servidor de descoberta de serviços, permitindo uma arquitetura altamente escalável. Principais características:

- **Portas Dinâmicas**: Os microsserviços (exceto o Gateway) são configurados com `server.port: 0`, fazendo com que o Spring Boot aloque portas aleatórias disponíveis a cada inicialização. Isso permite:
  - Executar múltiplas instâncias do mesmo serviço sem conflitos de porta
  - Facilitar a escalabilidade horizontal (adicionar mais instâncias conforme necessário)
  - Balanceamento de carga automático entre as instâncias

- **Identificação de Instâncias**: Cada instância é registrada no Eureka com um ID único:
  ```
  instance-id: ${spring.application.name}:${spring.application.instance_id:${random.value}}
  ```

- **Comunicação Transparente**: Os serviços se comunicam pelo nome da aplicação, não por portas específicas:
  - O Eureka traduz as chamadas para as instâncias apropriadas
  - O client-side load balancing permite distribuição de carga

### 🌉 Gateway com Porta Fixa
Enquanto os microsserviços usam portas dinâmicas, o API Gateway é configurado com uma porta fixa (8080):

- **Ponto de Entrada Único**: Todas as requisições externas passam pelo Gateway na porta 8080
- **Roteamento Inteligente**: O Gateway utiliza o Eureka para descobrir as instâncias dos serviços
- **Transparência para o Cliente**: Os clientes não precisam conhecer as portas dinâmicas dos serviços internos

Essa combinação de Gateway com porta fixa e microsserviços com portas dinâmicas oferece o melhor dos dois mundos: estabilidade no ponto de acesso e flexibilidade na arquitetura interna.

### 1️⃣ Fluxo de Cadastro de Pet e Agendamentos Automáticos
1. Cliente envia `POST /api/pets` com dados do pet
2. Serviço PetsGFT busca imagem na API externa (TheCatAPI ou TheDogAPI)
3. PetsGFT salva o pet no banco de dados MySQL
4. PetsGFT publica mensagem no RabbitMQ na fila `pet.created`
5. MsCareScheduling consome a mensagem e cria agendamentos automáticos baseados na idade do pet
6. Para cada agendamento criado, é publicada uma mensagem na fila `appointment.created`
7. MsNotificationEmail consome a mensagem e envia e-mail ao tutor

### 2️⃣ Fluxo de Agendamento Manual
1. Cliente envia `POST /api/appointments` com dados do agendamento
2. MsCareScheduling cria um registro "pendente" do agendamento
3. MsCareScheduling publica mensagem solicitando informações do pet na fila `pet.info.request`
4. PetsGFT consome a mensagem e responde com as informações na fila `pet.info.response`
5. MsCareScheduling finaliza o agendamento e publica na fila `appointment.created`
6. MsNotificationEmail envia e-mail de confirmação para o tutor

## 🔐 Configurações Adicionais

### ⚙️ Principais Configurações dos Microsserviços

#### 🔍 EurekaServer (Service Discovery)
```yaml
spring:
  application:
    name: EurekaServer
  security:
    user:
      name: admin
      password: admin

server:
  port: 8761

eureka:
  client:
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
  server:
    renewal-threshold-update-interval-ms: 60000
    eviction-interval-timer-in-ms: 60000
  instance:
    lease-renewal-interval-in-seconds: 10
    lease-expiration-duration-in-seconds: 30
```

#### 🐾 PetsGFT (Cadastro de Pets)
```yaml
spring:
  application:
    name: mspets
  datasource:
    url: jdbc:mysql://localhost:3306/db_pets?createDatabaseIfNotExist=true&serverTimezone=America/Sao_Paulo&useSSL=false
    username: root
    password: root
  rabbitmq:
    host: localhost
    port: 5673
    username: guest
    password: guest

server:
  port: 0

eureka:
  client:
    service-url:
      defaultZone: http://admin:admin@localhost:8761/eureka/
    fetch-registry: true
    register-with-eureka: true
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${spring.application.instance_id:${random.value}}

dogapi:
  user: root
  password: root

catapi:
  user: root
  password: root
```

#### 📅 MsCareScheduling (Agendamentos)
```yaml
spring:
  application:
    name: mscarescheduling
  datasource:
    url: jdbc:mysql://localhost:3306/db_appointment?createDatabaseIfNotExist=true&serverTimezone=America/Sao_Paulo&useSSL=false
    username: root
    password: root
  rabbitmq:
    host: localhost
    port: 5673
    username: guest
    password: guest

server:
  port: 0

eureka:
  client:
    service-url:
      defaultZone: http://admin:admin@localhost:8761/eureka/
    fetch-registry: true
    register-with-eureka: true
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${spring.application.instance_id:${random.value}}
```

#### 📨 MsNotificationEmail (Notificações)
```yaml
spring:
  application:
    name: MsNotificationEmail
  rabbitmq:
    host: localhost
    port: 5673
    username: guest
    password: guest
    listener:
      simple:
        retry:
          enabled: true
          initial-interval: 1000
          max-attempts: 3
          max-interval: 10000
          multiplier: 2.0

server:
  port: 0

notification:
  host_name: "127.0.0.1"
  host_port: "1025"
  port: "1025"

eureka:
  client:
    service-url:
      defaultZone: http://admin:admin@localhost:8761/eureka/
    fetch-registry: true
    register-with-eureka: true
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${spring.application.instance_id:${random.value}}
```

#### 🌐 MsCloudGateway (API Gateway)
```yaml
spring:
  application:
    name: mscloudgateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true

server:
  port: 8080

eureka:
  client:
    fetch-registry: true
    register-with-eureka: true
    service-url:
      defaultZone: http://admin:admin@localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

### 📧 Configuração do Serviço de E-mail
Para ambiente de desenvolvimento, o sistema utiliza MailHog:
- **Host**: mailhog
- **Port**: 1025
- **Interface Web**: http://localhost:8025

### 🐇 Configuração do RabbitMQ
- **Host**: rabbitmq
- **Port**: 5673
- **Management UI**: http://localhost:15673
- **Usuário**: guest
- **Senha**: guest

### 🗄️ Configuração do MySQL
- **Host**: mysql
- **Port**: 3306
- **Databases**:
  - **db_pets**: Para o microsserviço de cadastro
  - **db_appointment**: Para o microsserviço de agendamento
- **Usuário**: root
- **Senha**: root

## 🤝 Contribuindo
1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Faça commit das alterações (`git commit -m 'Adiciona nova funcionalidade'`)
4. Faça push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## Execuções 

![img_4.png](img_4.png)in
![img_3.png](img_3.png)

## 👥 Desenvolvedora
- Grazielle Ferreira - [GitHub](https://github.com/seu-usuario)

---


