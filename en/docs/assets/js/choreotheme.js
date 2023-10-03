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
            event.preventDefault(); // Prevent the default form submission behavior
            var searchTerm = textInput.value;
         //   const arrayFromCollection = Array.from(document.links);
         //   localStorage.setItem('searchResults', arrayFromCollection);
            var searchPage="/search-results.html" +"?search_term="+searchTerm;
            document.location.href=searchPage;

            // You can add more code here to handle the form submission
        }
    });

    function onSearchLoad() {

        function getQueryParamValue(paramName) {
            const urlParams = new URLSearchParams(window.location.search);
            return urlParams.get(paramName);
        }

        // Get the value of the 'value' query parameter
        const valueQueryParam = getQueryParamValue('value');

        // Set the value of the text input if the query parameter is present
        if (valueQueryParam !== null) {
            document.getElementById('textInput').value = valueQueryParam;
        } 
      
        // Reset stack and render results
        this.stack_ = [];
      
        result.forEach((items, ref) => {
          const doc = this.docs_.get(ref);
      
          // Create a new list item element
          const listItem = document.createElement("li");
          listItem.className = "md-search-result__item";
      
          // Create a link element
          const link = document.createElement("a");
          link.href = doc.location;
          link.title = doc.title;
          link.className = "md-search-result__link";
      
          // Create an article element
          const article = document.createElement("article");
          article.className = "md-search-result__article md-search-result__article--document";
      
          // Create an h1 element for the title
          const title = document.createElement("h1");
          title.className = "md-search-result__title";
          title.innerHTML = doc.title.replace(match, highlight);
      
          // Create a p element for the teaser (if doc.text exists)
          if (doc.text.length) {
            const teaser = document.createElement("p");
            teaser.className = "md-search-result__teaser";
            teaser.innerHTML = doc.text.replace(match, highlight);
            article.appendChild(teaser);
          }
      
          // Append the title and article to the link
          link.appendChild(title);
          link.appendChild(article);
      
          // Append the link to the list item
          listItem.appendChild(link);
      
          // Render sections for the article
          const sections = items.map(item => {
            const section = this.docs_.get(item.ref);
            const sectionLink = document.createElement("a");
            sectionLink.href = section.location;
            sectionLink.title = section.title;
            sectionLink.className = "md-search-result__link";
            sectionLink.setAttribute("data-md-rel", "anchor");
      
            const sectionArticle = document.createElement("article");
            sectionArticle.className = "md-search-result__article";
      
            const sectionTitle = document.createElement("h1");
            sectionTitle.className = "md-search-result__title";
            sectionTitle.innerHTML = section.title.replace(match, highlight);
      
            if (section.text.length) {
              const sectionTeaser = document.createElement("p");
              sectionTeaser.className = "md-search-result__teaser";
              sectionTeaser.innerHTML = truncate(section.text.replace(match, highlight), 400);
              sectionArticle.appendChild(sectionTeaser);
            }
      
            sectionLink.appendChild(sectionArticle);
            listItem.appendChild(sectionLink);
          });
      
          // Append the listItem to the search result list
          const ulElement = document.getElementById("md-search-result__list");
          ulElement.appendChild(listItem);
        });
      
        const serializedArray = localStorage.getItem("searchResults");
        const arrayFromStorage = JSON.parse(serializedArray);
      }
      