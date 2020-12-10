# Carrito de Compras MercadoPago SDK

## Como funciona?

Esta aplicacion fue hecha para demostrar el funcionamiento del SDK de mercadopago como gateway de pagos.
Hoy en dia, todo comercio que desea vender sus productos por internet, debe poder ofrecer la mejor experiencia de pago a sus clientes garantizando transparencia en las operaciones.
Es por eso, que es necesario integrarse con algun gateway de pagos que sea PCI Compliance, para garantizar seguridad en las transacciones, encriptando los datos financieros y evitando que se persistan en los servidores del comercio.
MercadoPago ofrece un servicio con una integracion bastante sencilla y en pocos minutos es posible tener un carrito de compras en produccion.

[SDK's de MercadoPago disponiles](https://www.mercadopago.com.ar/developers/es/guides/sdks)

Podemos dividir la integracion en dos partes:
1. Client-Side
2. Server-Side

# Client-Side

Para la integracion Client-Side es necesario instalar MercadoPago.js para hacer llamadas al token de tarjetas y obtener otras funcionalidades en tus integraciones.

```html
<script src="https://secure.mlstatic.com/sdk/javascript/v1/mercadopago.js"></script>              
```

Podemos encontrar las [credenciales](https://www.mercadopago.com.ar/developers/panel/credentials) de nuestra Aplicacion en el siguiente link.
Ahi veremos, por un lado la clave publica (PUBLIC_KEY) con la que se tokenizaran los datos de la tarjeta. Esta clave se utiliza solo en el cliente.
No te preocupes, puede ser visible.
Por otro lado, vamos a encontrar una clave privada (ACCESS_TOKEN). Esta clave privada, nos permitira obtener informacion de los pagos de nuestro comercio, asi como recibir notificaciones via [Webhook](https://www.mercadopago.com.ar/developers/es/guides/notifications/webhooks).
Esta clave sera utilizado por el SDK del backend.

Para recibir pagos con tarjeta, debemos capturar los datos de forma segura utilizando el Tokenizer.

1) Establece el viewport agregando el siguiente código dentro de la etiqueta `<head>` de tu sitio Web:
   
```html
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
```
2) Este fragmento de código HTML insertará un botón de pago. Cuando el comprador presione el botón se mostrará el checkout. Incluye el siguiente código en el lugar donde va a estar ubicado el botón dentro de tu sitio Web:

```html
<form action="/pay" method="POST">
  <script
    src="https://www.mercadopago.com.ar/integrations/v1/web-tokenize-checkout.js"
    data-public-key="ENV_PUBLIC_KEY"
    data-transaction-amount="100.00">
  </script>
</form>
```
Debemos colocar la `ENV_PUBLIC_KEY` que encontramos en la seccion de Credenciales.

# Server-Side

El uso de las SDKs de backend es opcional. Con ellas puedes obtener funcionalidades server-side de nuestras soluciones de cobros online como:

* Crear y conocer el estado de diferentes pagos
* Integrar pagos con tarjetas u otros medios de pago
* Hacer devoluciones o cancelar pagos

En este ejemplo, vamos a utilizar la SDK en [Java](https://github.com/mercadopago/dx-java).

3) El Web Tokenize Checkout hará un POST a la URL que hayas definido en el atributo action del fragmento de código HTML (En el ejemplo: /pay) con ciertos datos. Debes utilizar dichos datos para realizar el pago. Es decir, el pago se realiza desde el Backend.

```java
String cardToken = ctx.formParam("token");
String issuerId = ctx.formParam("issuer_id");
String installments = ctx.formParam("installments");
String paymentMethodId = ctx.formParam("payment_method_id");
```
Como podemos observar,por cuestiones de seguridad el monto no viaja en el formulario.

4) Realizamos el pago utilizando el SDK de nuestra eleccion. Es simplemente realizar una API call.

```java
payment = new Payment()
    .setPaymentMethodId(paymentMethodId)
    .setTransactionAmount(100f)
    .setInstallments(Integer.valueOf(installments))
    .setToken(cardToken)
    .setIssuerId(issuerId)
    .setPayer(new Payer().setEmail("user@test.com"));

payment.save(MPRequestOptions.builder().setAccessToken(ACCESS_TOKEN).build());
```

Los campos requeridos a enviar son `token`, `transaction_amount`, `payment_method_id` y el `payer.email`.

## Tarjetas de Prueba

Para realizar pagos de prueba (con tus credenciales de TEST), es necesario que utilices [tarjetas de prueba](https://www.mercadopago.com.ar/developers/es/guides/online-payments/checkout-api/testing).

Para probar distintos resultados de pago, completa el dato que quieras en el nombre del titular de la tarjeta:

* APRO: Pago aprobado.
* CONT: Pago pendiente.
* OTHE: Rechazado por error general.
* CALL: Rechazado con validación para autorizar.
* FUND: Rechazado por monto insuficiente.
* SECU: Rechazado por código de seguridad inválido.
* EXPI: Rechazado por problema con la fecha de expiración.
* FORM: Rechazado por error en formulario.




