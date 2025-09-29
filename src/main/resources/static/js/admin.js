console.log("admin user");

/*document
  .querySelector("#image_file_input")
  .addEventListener("change", function (event) {
    let file = event.target.files[0];
    let reader = new FileReader();
    reader.onload = function () {
      document
        .querySelector("#upload_image_preview")
        .setAttribute("src", reader.result);
    };
    reader.readAsDataURL(file);
  });
*/
   
      let guestIndex = 1; // 0 is already rendered
      const maxGuests = 4;

	  document.getElementById('addGuestBtn').addEventListener('click', function () {
	      const container = document.getElementById('guest-container');
	      const guestBlocks = container.querySelectorAll('.guest-entry');

	      if (guestBlocks.length >= maxGuests) {
	          alert("Maximum 4 guests allowed.");
	          return;
	      }

	      const firstGuest = guestBlocks[0];
	      const newGuest = firstGuest.cloneNode(true);

	      // Update input names and clear values except files
	      newGuest.querySelectorAll('input, select, textarea').forEach(el => {
	          const oldName = el.getAttribute('name');
	          if (oldName) {
	              const updatedName = oldName.replace(/\[\d+\]/, `[${guestIndex}]`);
	              el.setAttribute('name', updatedName);
	          }

	          if (el.type === 'file') {
	              // Remove the file input element
	              el.parentNode.removeChild(el);
	          } else {
	              el.value = '';
	          }
	      });

	      // Now add new empty file inputs manually at the original positions
	      // For "Document Front"
	      const docFrontLabel = newGuest.querySelector('label[for="documentFront"]') || 
	                           newGuest.querySelector('label:contains("Document Front")');
	      if (docFrontLabel) {
	          const newFileInputFront = document.createElement('input');
	          newFileInputFront.type = 'file';
	          newFileInputFront.name = `customerDetails[${guestIndex}].documentFront`;
	          newFileInputFront.className = 'doc-front-field bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500';
	          docFrontLabel.parentNode.appendChild(newFileInputFront);
	      }

	      // For "Document Back"
	      const docBackLabel = newGuest.querySelector('label[for="documentBack"]') || 
	                          newGuest.querySelector('label:contains("Document Back")');
	      if (docBackLabel) {
	          const newFileInputBack = document.createElement('input');
	          newFileInputBack.type = 'file';
	          newFileInputBack.name = `customerDetails[${guestIndex}].documentBack`;
	          newFileInputBack.className = 'doc-back-field bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500';
	          docBackLabel.parentNode.appendChild(newFileInputBack);
	      }

	      // Update guest heading number
	      const guestNumEl = newGuest.querySelector('.guest-number');
	      if (guestNumEl) {
	          guestNumEl.textContent = guestIndex + 1;
	      }

	      container.appendChild(newGuest);
	      guestIndex++;
	  });

  
