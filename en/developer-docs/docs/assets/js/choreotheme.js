/*!
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
* Handle opening external links in a new tab
*/
/* 
 * Initialize highlightjs 
 */
const form = document.getElementById("search");
const textInput = document.getElementById("query");

    // Get a reference to the input field
const nameInput = document.getElementById("query");
window.addEventListener("DOMContentLoaded", function() {
    hljs.initHighlightingOnLoad();
});
(function() {
    if(document.querySelector('.tab-selector')){
        document.querySelector('.tab-selector').addEventListener('click', function(e) {
            // Show hide tab content next to the clicked tab
            var tabContentToShow = e.target.nextElementSibling;
            if(tabContentToShow.style.display === 'none') {
                tabContentToShow.style.display = 'block';
            } else {
                tabContentToShow.style.display = 'none';
            }
        });
    }
})();


    // Add an event listener to capture the Enter key press
    window.addEventListener('keydown', function(event) {
        if (event.key === 'Enter' && event.target.id === "query")  {
            var searchTerm = textInput.value;
            var pathname = this.window.location.pathname;
            var origin = this.window.location.origin;
            var searchPage= "search-results.html" + "?search_term=" + searchTerm;
            if(pathname != null && pathname.startsWith("/choreo/docs/")){
               
                searchPage = origin + "/choreo/docs/" + searchPage;
            } else{
                searchPage = origin + "/"+ searchPage;
            }

         //   const arrayFromCollection = Array.from(document.links);
         //   localStorage.setItem('searchResults', arrayFromCollection);
            document.location.href=searchPage;

            // You can add more code here to handle the form submission
        }
    });
