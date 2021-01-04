# Comments #

## Setup

* Add your keys to marvelPublicApiKey and marvelPrivateApiKey in your global gradle.properties file.

## Highlights

* I used MVI with MvRx, which replaces LiveData with an immutable State management.
* I used Retrofit 2, RxJava 2, Dagger 2, AndroidX, SqlDelight, Navigation and Material components.
* I added one Unit test.
* Espresso tests to cover some situations. Added Barista to my arsenal in the process.

## A note on production readiness

* In my opinion an app is production ready when the product owner (in this case the interviewer)
finds it sufficient. So by definition I cannot know when this is true. I understand in this case my
definition of done is tested, so I tried to add everything I can in a timely manner.
However, I still feel this is not production ready, because it needs at least a second pair of eyes
and a confirmation of readiness from that. Once this happens, I'm more confident.

## Structure

* Note the use of buildSrc, the standard way to add dependencies with Gradle. In a modularized
  setting I would also add reusable Gradle plugins here, so that the module build files are tiny.
* Most of the styles, base classes, etc. are coming from BaBeStudiosBase, my base library. I
overrode it in a few instances.
* Lately I use a modular structure, but for this app I use only packages instead.
* Common classes within the app go to the common package (or module). I often extract classes from
here to the base library, if I need them elsewhere.
* Dependency related files go to the 'di' package. The Dagger component can be obtained from the
App class. No Component dependencies were added for this small project. I access the component from
the Activity, from here the ViewModel can be injected such, that they don't know anything about
each other.
* Navigation related files go to the navigation package. This separation was needed for
modularization only, but I kept it for consistency.
* Data package contains everything related to networking and data storage.
* Views, ViewModels and State go into the ui package. There is one Activity per feature, the
screens are represented by Fragments. There is one ViewModel per Activity, which also holds
the State.
* Lists are simplified/standardized by using the Factory and Visitable patterns, where the
ViewHolder types are represented by their layouts. This makes them easily extendable. I added
DiffUtil callbacks for each list.

## Some design choices

* Status bar text color is not mutable in Android (only dark-light). It should be white here.
* I added an AppBar to the Hero Detail screen and moved the hero name into the toolbar.
* I haven't worked on landscape layouts, so on a phone they don't look good (yet).
* The caching strategy is storage first, then network always updating. I think this is appropriate
for this data source, but is new to me, so the app might have some subtle bugs still.

## Room for improvement

* Proper storage and decoupling of it. I learnt more advanced SqlDelight usage with this assignment.
 Probably I will need more work on mitigating problems with the increased complexity. One area 
 definitely needs improvement is error handling.
* More tests, better setup for test (less repetition, etc.)e
* I used MultiStateView to represent view states. I understand this is not always ideal, because
its loading and error states are disruptive. Only the initial loading is displayed. I will review
this library in the future.
* I didn't have time left for a launcher icon.
* Etc.

## Other notes

* MvRx doesn't support Navigation component lifecycle fully at the moment, so I had to handle
orientation changes manually. See BaseFragment and its implementations.
* I tried to replace EndlessRecyclerViewScrollListener with the AAC Paging library, but it
doesn't work nicely with MvRx. For now I think it's not worth the effort, but I will have to 
search for an alternative in the future.
* I tried to switch to Coroutines at the end, but it requires a huge effort without official
support in MvRx, so I reverted this. They will however support it in just a few weeks in 2.0.
