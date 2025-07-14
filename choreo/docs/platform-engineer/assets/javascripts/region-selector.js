/**
 * Choreo Region Selector
 *
 * This script intercepts clicks on console.choreo.dev and devportal.choreo.dev links and shows a popup
 * that asks users to choose between US and EU regions with a "remember me" option.
 */
document.addEventListener('DOMContentLoaded', function() {
    // Create the modal dialog
    const modal = document.createElement('div');
    modal.id = 'region-selector-modal';
    modal.style.display = 'none';
    modal.style.position = 'fixed';
    modal.style.zIndex = '9999';
    modal.style.left = '0';
    modal.style.top = '0';
    modal.style.width = '100%';
    modal.style.height = '100%';
    modal.style.backgroundColor = 'rgba(0, 0, 0, 0.5)';
    modal.style.overflow = 'auto';

    // Create the modal content
    const modalContent = document.createElement('div');
    modalContent.className = 'modal-content';
    modalContent.style.backgroundColor = '#ffffff';
    modalContent.style.margin = '15% auto';
    modalContent.style.padding = '20px';
    modalContent.style.border = '1px solid #888';
    modalContent.style.borderRadius = '8px';
    modalContent.style.width = '300px';
    modalContent.style.maxWidth = '90%';
    modalContent.style.boxShadow = '0 4px 15px 0 rgba(0, 0, 0, 0.2)';

    // Add header
    const header = document.createElement('h3');
    header.textContent = 'Select Choreo Region';
    header.style.marginTop = '0';
    header.style.color = '#2c3e50';
    header.style.fontWeight = '600';
    header.style.fontSize = '15px';
    modalContent.appendChild(header);

    // Add description
    const description = document.createElement('p');
    description.textContent = 'Please select the Choreo region you want to access:';
    description.style.color = '#5f6368';
    description.style.marginBottom = '16px';
    description.style.fontSize = '13px';
    modalContent.appendChild(description);

    // Add radio buttons container
    const radioContainer = document.createElement('div');
    radioContainer.style.display = 'flex';
    radioContainer.style.gap = '20px';
    radioContainer.style.justifyContent = 'left';
    radioContainer.style.marginBottom = '16px';

    // Add US region option
    const usContainer = document.createElement('div');
    usContainer.style.display = 'flex';
    usContainer.style.alignItems = 'center';
    usContainer.style.cursor = 'pointer';

    const usRadio = document.createElement('input');
    usRadio.type = 'radio';
    usRadio.name = 'region';
    usRadio.value = 'us';
    usRadio.id = 'region-us';
    usRadio.style.marginRight = '8px';
    usRadio.checked = true; // Default to US

    const usFlag = document.createElement('span');
    usFlag.textContent = '🇺🇸';
    usFlag.style.fontSize = '18px';
    usFlag.style.marginRight = '6px';

    const usLabel = document.createElement('label');
    usLabel.htmlFor = 'region-us';
    usLabel.textContent = 'US';
    usLabel.style.cursor = 'pointer';
    usLabel.style.fontWeight = '500';
    usLabel.style.color = '#2c3e50';
    usLabel.style.fontSize = '14px';

    usContainer.appendChild(usRadio);
    usContainer.appendChild(usFlag);
    usContainer.appendChild(usLabel);

    // Add EU region option
    const euContainer = document.createElement('div');
    euContainer.style.display = 'flex';
    euContainer.style.alignItems = 'center';
    euContainer.style.cursor = 'pointer';

    const euRadio = document.createElement('input');
    euRadio.type = 'radio';
    euRadio.name = 'region';
    euRadio.value = 'eu';
    euRadio.id = 'region-eu';
    euRadio.style.marginRight = '8px';

    const euFlag = document.createElement('span');
    euFlag.textContent = '🇪🇺';
    euFlag.style.fontSize = '18px';
    euFlag.style.marginRight = '6px';

    const euLabel = document.createElement('label');
    euLabel.htmlFor = 'region-eu';
    euLabel.textContent = 'EU';
    euLabel.style.cursor = 'pointer';
    euLabel.style.fontWeight = '500';
    euLabel.style.color = '#2c3e50';
    euLabel.style.fontSize = '14px';

    euContainer.appendChild(euRadio);
    euContainer.appendChild(euFlag);
    euContainer.appendChild(euLabel);

    radioContainer.appendChild(usContainer);
    radioContainer.appendChild(euContainer);
    modalContent.appendChild(radioContainer);

    // Add click handlers for radio containers
    usContainer.addEventListener('click', function() {
        usRadio.checked = true;
    });

    euContainer.addEventListener('click', function() {
        euRadio.checked = true;
    });

    // Add remember me checkbox
    const rememberContainer = document.createElement('div');
    rememberContainer.style.display = 'flex';
    rememberContainer.style.alignItems = 'center';
    rememberContainer.style.marginBottom = '20px';

    const rememberCheckbox = document.createElement('input');
    rememberCheckbox.type = 'checkbox';
    rememberCheckbox.id = 'remember-region';
    rememberCheckbox.style.marginRight = '10px';

    const rememberLabel = document.createElement('label');
    rememberLabel.htmlFor = 'remember-region';
    rememberLabel.textContent = 'Remember my preference';
    rememberLabel.style.color = '#5f6368';
    rememberLabel.style.fontSize = '13px';
    rememberLabel.style.cursor = 'pointer';

    rememberContainer.appendChild(rememberCheckbox);
    rememberContainer.appendChild(rememberLabel);
    modalContent.appendChild(rememberContainer);

    // Add action buttons container
    const actionButtonsContainer = document.createElement('div');
    actionButtonsContainer.style.display = 'flex';
    actionButtonsContainer.style.justifyContent = 'flex-end';
    actionButtonsContainer.style.gap = '12px';

    // Add Cancel button
    const cancelButton = document.createElement('button');
    cancelButton.textContent = 'Cancel';
    cancelButton.style.padding = '10px 20px';
    cancelButton.style.backgroundColor = 'transparent';
    cancelButton.style.color = '#5f6368';
    cancelButton.style.border = '2px solid #e1e5e9';
    cancelButton.style.borderRadius = '4px';
    cancelButton.style.cursor = 'pointer';
    cancelButton.style.fontWeight = '500';
    cancelButton.style.transition = 'all 0.2s ease';

    // Add Proceed button
    const proceedButton = document.createElement('button');
    proceedButton.textContent = 'Proceed';
    proceedButton.style.padding = '10px 20px';
    proceedButton.style.backgroundColor = '#545CEC';
    proceedButton.style.color = 'white';
    proceedButton.style.border = 'none';
    proceedButton.style.borderRadius = '4px';
    proceedButton.style.cursor = 'pointer';
    proceedButton.style.fontWeight = '500';
    proceedButton.style.transition = 'all 0.2s ease';

    // Add hover effects for buttons
    cancelButton.addEventListener('mouseenter', function() {
        cancelButton.style.borderColor = '#545CEC';
        cancelButton.style.color = '#545CEC';
    });
    cancelButton.addEventListener('mouseleave', function() {
        cancelButton.style.borderColor = '#e1e5e9';
        cancelButton.style.color = '#5f6368';
    });

    proceedButton.addEventListener('mouseenter', function() {
        proceedButton.style.backgroundColor = '#4248c7';
    });
    proceedButton.addEventListener('mouseleave', function() {
        proceedButton.style.backgroundColor = '#545CEC';
    });

    actionButtonsContainer.appendChild(cancelButton);
    actionButtonsContainer.appendChild(proceedButton);
    modalContent.appendChild(actionButtonsContainer);

    // Add the modal content to the modal
    modal.appendChild(modalContent);

    // Add the modal to the body
    document.body.appendChild(modal);

    // Variable to store the clicked URL
    let clickedUrl = '';

    // Function to show the modal
    function showModal(url) {
        clickedUrl = url;
        modal.style.display = 'block';
    }

    // Function to hide the modal
    function hideModal() {
        modal.style.display = 'none';
    }

    // Function to redirect to the selected region
    function redirectToRegion() {
        const selectedRegion = document.querySelector('input[name="region"]:checked').value;
        const remember = rememberCheckbox.checked;

        // Save the preference if remember is checked
        if (remember) {
            localStorage.setItem('choreoRegionPreference', selectedRegion);
        }

        // Open in the appropriate URL in a new tab
        let targetUrl;
        if (selectedRegion === 'us') {
            targetUrl = clickedUrl;
        } else if (selectedRegion === 'eu') {
            // Handle both console and devportal URLs
            if (clickedUrl.includes('console.choreo.dev')) {
                targetUrl = clickedUrl.replace('console.choreo.dev', 'console.eu.choreo.dev');
            } else if (clickedUrl.includes('devportal.choreo.dev')) {
                targetUrl = clickedUrl.replace('devportal.choreo.dev', 'devportal.eu.choreo.dev');
            } else {
                targetUrl = clickedUrl;
            }
        }

        // Open the target URL in a new tab
        window.open(targetUrl, '_blank');
    }

    // Handle Cancel button click
    cancelButton.addEventListener('click', function() {
        hideModal();
    });

    // Handle Proceed button click
    proceedButton.addEventListener('click', function() {
        redirectToRegion();
        hideModal();
    });

    // Handle radio button changes
    usRadio.addEventListener('change', function() {
        // Radio button changed
    });
    euRadio.addEventListener('change', function() {
        // Radio button changed
    });

    // Intercept clicks on Choreo console links
    document.addEventListener('click', function(event) {
        // Check if the clicked element is a link
        let element = event.target;

        // If the clicked element is not an anchor, check if it's a child of an anchor
        while (element && element.tagName !== 'A') {
            element = element.parentElement;
        }

        // If we found an anchor, check if it's a Choreo console link
        if (element && element.tagName === 'A') {
            const href = element.getAttribute('href');

            // Check if the link is a Choreo console or devportal link
            if (href && (href.includes('console.choreo.dev') || href.includes('devportal.choreo.dev'))) {
                // Prevent the default action
                event.preventDefault();

                // Check if there's a saved preference
                const savedPreference = localStorage.getItem('choreoRegionPreference');

                if (savedPreference) {
                    // Use the saved preference and open in new tab
                    if (savedPreference === 'us') {
                        window.open(href, '_blank');
                    } else if (savedPreference === 'eu') {
                        let targetUrl = href;
                        if (href.includes('console.choreo.dev')) {
                            targetUrl = href.replace('console.choreo.dev', 'console.eu.choreo.dev');
                        } else if (href.includes('devportal.choreo.dev')) {
                            targetUrl = href.replace('devportal.choreo.dev', 'devportal.eu.choreo.dev');
                        }
                        window.open(targetUrl, '_blank');
                    }
                } else {
                    // Show the modal
                    showModal(href);
                }
            }
        }
    });

    // Close the modal when clicking outside of it
    window.addEventListener('click', function(event) {
        if (event.target === modal) {
            hideModal();
        }
    });
});
