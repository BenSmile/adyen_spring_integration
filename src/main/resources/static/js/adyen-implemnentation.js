async function initCheckout() {

  try {

    const clientKey = document.getElementById("clientKey").innerHTML;

    const paymentMethodsResponse = await callServer('/api/getPaymentMethods');
    const config = {
      paymentMethodsResponse,
      clientKey,
      local: 'en_US',
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
            value: 1000,
            currency: 'EUR'
          }
        }
      },
      onSubmit: (state, component) => {

        if (state.isValid) {
          handleSubmission(state, component, '/api/initiatePayment')
        }
      },
      onAdditionalDetails: (state, component) => {
        handleSubmission(state, component, '/api/submitAddtionnalDetails')

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

async function handleSubmission(state, component, url) {


  try {

    const res = await callServer(url, state.data);
    handleServerResponse(res, component);
  } catch (error) {
    console.log(error)
    alert("Error occured. Look at the console for more details")
  }
}

function handleServerResponse(res, component) {

  if (res.action) {
    component.handleAction(res.action)
  } else {

    switch (res.resultCode) {
      case 'Authorised':
        window.location.href = '/result/succes'
        break;
      case 'Pending':
      case 'Received':
        window.location.href = '/result/pending';
        break;

      case 'Refused':
        window.location.href = '/result/failed'
        break;

      default:
        window.location.href = '/result/error';
        break;

    }

  }
}

initCheckout();
