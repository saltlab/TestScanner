/***************************************************
 * This is mocky.js version 0.9                    *
 ***************************************************
 *  Copyright (c) 2015, Jim Driscoll
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of Mocky nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/* new Mocky(o, function(mocky) {...})
/* new Mocky(o)
 *
 * Overrides some of the properties on object "o" until the end of
 * the provided function. You can define which methods to override
 * inside the function by calling methods on the mocky object itself.
 *
 * If you don't provide a function, this will just return the mocky
 * object. In this case you need to call mocky.unmock() yourself
 * when done, which can be useful during async testing.
 */
function Mocky(o, f) {
  if(!o) throw "You must provide an object to mock";
  this.mocking = o;
  this.mocked = {};
  this.missingProperties = {};
  if(f) {
    this.usingCallback = true;
    f(this);
    this.unmock();
  }
}
Mocky.prototype = {
  /* .mock(m)
   *
   * Given an object m, replaces the counterpart properties on o
   * with those on m. You can call this multiple times.
   */
  mock: function(m) {
    for(var k in m) {
      if(!this.mocked.hasOwnProperty(k)) this.mocked[k] = this.mocking[k];
      if(!this.mocking.hasOwnProperty(k)) this.missingProperties[k] = true;
      this.mocking[k] = m[k];
    }
  },
  /* .unmock()
   *
   * This should only be called implicitly, or in async mode. Drops
   * all the mocked properties.
   */
  unmock: function() {
    if(this.usingCallback) console.log("In callback mode, you should not need to call unmock() explicitly");
    for(var k in this.mocked) {
      if(this.missingProperties[k]) {
        delete this.mocking[k];
      } else {
        this.mocking[k] = this.mocked[k];
      }
    }
    this.mocked = {};
    this.missingProperties = {};
  }
};
