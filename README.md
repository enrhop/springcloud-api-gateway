# springcloud-api-gateway

This sample project shows how to implement a spring cloud based api gateway securing access to a spring webflux based rest webservice. The api gateway is using OAuth 2 (authorization code flow) to authenticate the user user who is trying to access the rest service. After the user is  successfully authenticated, the api gateway maintans a session. Each further request must then send the cookie with it.
