> Update 2017-02-01: Got a VERY quick and dirty version for the new React Navigation library going. If you are looking for the old one for NavigationExperimental it is on the branch [old-re-navigate](https://github.com/vikeri/re-navigate/tree/old-re-navigate)

# re-navigate
> Example of React Navigation with [re-frame](https://github.com/Day8/re-frame)/[re-natal](https://github.com/drapanjanas/re-natal/)


This example uses React Native's new Navigation component [React Navigation](https://reactnavigation.org/) that eventually will replace the current React Native navigation solutions (or so they say).

The one thing that does not work is to read the state of the tabs in `re-frame`. If you do that the tab switching will flicker. So currently it is a hack that saves the state to the re-frame db but uses the Navigators own state management to actually update the state.

If someone wants to give the full `re-frame` solution a stab that can be found on commit 78c79aa.

Reading the state of the stack from re-frame works just fine.

## Example code

It is based on the scaffold from [re-natal](https://github.com/drapanjanas/re-natal/), almost everything is found in [re-navigate.ios.core](src/re_navigate/ios/core.cljs)

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
(in-ns 're-navigate.shared.main)
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
