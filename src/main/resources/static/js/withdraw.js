console.log("withdraw.js");
 const baseURL = "http://localhost:8081";
//const baseURL = "https://www.scm20.site";
const viewContactModal = document.getElementById("view_withdraw_modal");

// options with default values
const options = {
  placement: "bottom-right",
  backdrop: "dynamic",
  backdropClasses: "bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40",
  closable: true,
  onHide: () => {
    console.log("modal is hidden - withdraw");
  },
  onShow: () => {
    setTimeout(() => {
      contactModal.classList.add("scale-100");
    }, 50);
  },
  onToggle: () => {
    console.log("modal has been toggled - withdraw");
  },
};

// instance options object
const instanceOptions = {
  id: "view_withdraw_modal",
  override: true,
};

const contactModal = new Modal(viewContactModal, options, instanceOptions);

function openContactModal() {
  contactModal.show();
}

function closeContactModal() {
  contactModal.hide();
}

async function loadContactdata(id) {
  //function call to load data
  console.log(id);
  try {
	//console.log(`${baseURL}/api/get-withdrawal-amount`);
    const data = await (await fetch(`${baseURL}/api/get-withdrawal-amount`)).json();
    console.log(data);
	openContactModal();
	let div = document.getElementById("show_remaning_amount");
	 const netBalance = data.netBalance;
	 const netCashBalance = data.netCashBalance;
	 const netOnlineBalance = data.netOnlineBalance;

	 const htmlContent = `
	   <div class="overflow-x-auto">
	     <table class="min-w-full bg-white border border-gray-200 rounded-lg shadow">
	       <thead>
	         <tr class="bg-gray-100 text-gray-700 text-left">
	           <th class="py-2 px-4 border-b">Type</th>
	           <th class="py-2 px-4 border-b">Amount</th>
	         </tr>
	       </thead>
	       <tbody>
	         <tr>
	           <td class="py-2 px-4 border-b">Total Cash Amount</td>
	           <td class="py-2 px-4 border-b">${netCashBalance}</td>
	         </tr>
	         <tr class="bg-gray-50">
	           <td class="py-2 px-4 border-b">Total Online Amount</td>
	           <td class="py-2 px-4 border-b">${netOnlineBalance}</td>
	         </tr>
	         <tr>
	           <td class="py-2 px-4 border-b font-semibold">Total Remaining Amount</td>
	           <td class="py-2 px-4 border-b font-semibold">${netBalance}</td>
	         </tr>
	       </tbody>
	     </table>
	   </div>
	 `;


   	div.innerHTML = htmlContent;
  } catch (error) {
    console.log("Error: ", error);
  }
}

document.getElementById("withdraw_form").addEventListener("submit", async function (e) {
   e.preventDefault(); // prevent actual form submission

   const data = await (await fetch(`${baseURL}/api/get-withdrawal-amount`)).json();
   const netBalance = data.netBalance;
   const netCashBalance = data.netCashBalance;
   const netOnlineBalance = data.netOnlineBalance;

   const cashAmount = parseFloat(document.getElementById("cashAmount").value) || 0;
   const onlineAmount = parseFloat(document.getElementById("onlineAmount").value) || 0;
   const totalAmount = cashAmount + onlineAmount;
   const errorMessage = document.getElementById("errorMessage");

   // Clear previous error
   errorMessage.textContent = "";
   
   if(netBalance <= 0) {
	   errorMessage.textContent = "No balance available for withdrawal";
	   return;
   }
   
	if (cashAmount === 0 && onlineAmount === 0) {
		errorMessage.textContent = "Please enter at least one amount (Cash or Online)";
		return;
	}
   // Validation checks
   if (cashAmount > netCashBalance) {
     errorMessage.textContent = `Cash amount cannot exceed ₹${netCashBalance}`;
     return;
   }

   if (onlineAmount > netOnlineBalance) {
     errorMessage.textContent = `Online amount cannot exceed ₹${netOnlineBalance}`;
     return;
   }

   if (totalAmount > netBalance) {
     errorMessage.textContent = `Total amount (Cash + Online) cannot exceed ₹${netBalance}`;
     return;
   }	

   const submitBtn = document.getElementById("submitBtn");
   submitBtn.disabled = true;
   submitBtn.textContent = "Submitting...";

   const formData = {
     ownerName: document.getElementById("ownerName").value || null,
     cashAmount: parseFloat(document.getElementById("cashAmount").value) || 0,
     onlineAmount: parseFloat(document.getElementById("onlineAmount").value) || 0,
     withdrawDate: document.getElementById("withdrawDate").value,
     comment: document.getElementById("comment").value,
    // formToken: document.getElementById("formTokenInput").value
   };

   fetch(`${baseURL}/api/submit-withdrawal`, {
     method: "POST",
     headers: {
       "Content-Type": "application/json"
     },
     body: JSON.stringify(formData)
   })
   .then(async response => {
     if (!response.ok) {
       const errorText = await response.text();
       throw new Error(errorText || "Submission failed");
     }
     return response.text(); // or JSON depending on your backend
   })
   .then(result => {
     alert("Success: " + result);
	 document.getElementById("withdraw_form").reset();

	 // ✅ Reload page after short delay (optional)
	 setTimeout(() => {
	   window.location.reload();
	 }, 1000); // reload after 1 second
     // Optionally reset form or close modal
   })
   .catch(err => {
     document.getElementById("errorMessage").textContent = err.message;
     submitBtn.disabled = false;
     submitBtn.textContent = "Submit";
   });
 });
