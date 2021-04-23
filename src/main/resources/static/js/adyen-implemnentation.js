async function initCheckout() {

  try {

    const clientKey = document.getElementById("clientKey").innerHTML;

    const paymentMethodsResponse = await callServer('/api/getPaymentMethods');
    const config = {
      paymentMethodsResponse,
      clientKey,
      local: 'us_US',
      environment: 'test',
      showPayButton: true,
      paymentMethodsConfiguration: {
        ideal: {
          showImage: true
        },
        card: {
          hasHolderName: true,
          holdernameRequired: true,

          name: "Credit or debit card",
          amount: {
            value: 10,
            currency: 'EUR'
          }
        }
      },
      onsubmit: (state, component) => {

      },
      onAdditionalDetails: (state, component) => {

      }
    };


    // `spring.jackson.default-property-inclusion=non_null` needs to set in
    // src/main/resources/application.properties to avoid NPE here
    const checkout = new AdyenCheckout(config);
    checkout.create("dropin").mount(document.getElementById("dropin"));


  } catch (error) {
    console.log(error)
    alert("Error occured, Look at consolo for more details")
  }

}

async function callServer(url, data) {
  const res = await fetch(url, {
    method: 'POST',
    body: data ? JSON.stringify(data) : '',
    headers: {
      'Content-type': 'application/json'
    }
  });

  return await res.json();
}

initCheckout();
