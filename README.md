# Yard Incident Management Portal (YIMP)

## How to run
```
$ npm i
$ lein prod-build
```
Then run in iOS from xcode or `react-native run-ios`

## How to develop
YIMP is based on re-natal 0.2.34+.

```
re-natal use-figwheel
lein figwheel ios
react-native run-ios
```

Please, refer to [re-natal documentation](https://github.com/drapanjanas/re-natal/blob/master/README.md) for more information.

## Building a release package

```
lein prod-build
```

Edit AppDelegate.m and comment out the live reload:

```
//jsCodeLocation = [NSURL URLWithString:@"http://localhost:8081/index.ios.bundle?platform=ios&dev=true"];
 jsCodeLocation = [[NSBundle mainBundle] URLForResource:@"main" withExtension:@"jsbundle"];

```

## Adding an image

Copy image to `./images`. Be sure to add 2x and 3x versions (note the `@2x.png` and `@3x.png` suffixes).
1. Restart React Package Manager
2. Run `re-natil use-figwheel` to update deps
3. `lein figwheel ios` to keep developing

## Publish to App Store

### TestFlight

1. Update `config.cljs` and make sure app pointing to Heroku host.
1. run `lein prod-build`
1. Run fastlane:

```
fastlane beta
```

### App Store

TBC



## Tools

> Example of React Navigation with [re-frame](https://github.com/Day8/re-frame)/[re-natal](https://github.com/drapanjanas/re-natal/)

This example uses React Native's new Navigation component [React Navigation](https://reactnavigation.org/) that eventually will replace the current React Native navigation solutions (or so they say).

The one thing that does not work is to read the state of the tabs in `re-frame`. If you do that the tab switching will flicker. So currently it is a hack that saves the state to the re-frame db but uses the Navigators own state management to actually update the state.

If someone wants to give the full `re-frame` solution a stab that can be found on commit 78c79aa.

Reading the state of the stack from re-frame works just fine.

## Example code

It is based on the scaffold from [re-natal](https://github.com/drapanjanas/re-natal/), almost everything is found in [yimp.ios.core](src/re_navigate/ios/core.cljs)

## Run

Requirements:
- node & npm/yarn
- leiningen `brew install leiningen`
- re-natal & react-native-cli `npm install -g re-natal react-native-cli`

`cd` into the directory.

```
yarn && lein prod-build && react-native  run-ios --simulator "iPhone 7 Plus"
```


## Development Notes

* Using `props` and `this`: https://reagent-project.github.io/news/any-arguments.html

## Notes

- React (15.4.2)
- React Native (0.40.0)


## Contributing

Get into current namespace:

```
(in-ns 'yimp.shared.main)
```

Set current incident:
```clj
(dispatch [:set-current-incident {:description "test" :students [1] :follow_up false :location "Senior playground" :summary "Verbal dispute" :action_taken "Resolved through discussion"}])
```

```clj
(dispatch [:set-current-incident (clj->js {:description "betty" :students [1] :follow_up false :location "Senior playground" :summary "Verbal dispute" :action_taken "Resolved through discussion"})])
```

NOTE: Make sure that all of these values exist in the current pickers, otherwise
you'll get errors like "can't get the value of text..."


## Android

```
adb reverse tcp:8081 tcp:8081 # React Dev Server
adb reverse tcp:3000 tcp:3000 # Local API
```

## Credits

<a href="http://www.freepik.com/free-vector/children-playing-on-playground_992323.htm">Designed by Freepik</a>
