// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random fact to the page.
 */
function addRandomFact() {
  const greetings =
      ['I have six names', 'Forrest Gump is my favorite movie', 'I have a one year old Husky/Lab mix', 'I am obsessed with The Sims 4'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

/**
 * The above code is organized to show each individual step, but we can use an
 * ES6 feature called arrow functions to shorten the code. This function
 * combines all of the above code into a single Promise chain. You can use
 * whichever syntax makes the most sense to you.
 */
function getTranslation() {
    const lang = document.getElementById('language').value;
    console.log(lang);
    
    const resultContainer = document.getElementById('comments-container');
    resultContainer.innerText = 'Loading...';

    const params = new URLSearchParams();
    params.append('lang', lang);

    fetch('/data?' + params.toString(), {
        method: 'GET'
    }).then(response => response.json())
    .then((translatedMessage) => {
        resultContainer.innerText = '';
        translatedMessage.forEach((post) => {
        resultContainer.appendChild(
            createListElement(post.userName + ': ' + post.userComment));
        });
    });
    
}

/** Creates an <li> element containing text. */

function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}



