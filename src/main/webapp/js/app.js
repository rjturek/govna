/*global angular*/    // Stop jsLint from complaining about globally defined variables.

//var app = angular.module('govna', ['ui.bootstrap']);
var app = angular.module('govna', []);

app.controller('MainCtrl', function ($scope, $http) {
    'use strict';
    var part1 = 'Wo';
    var part2 = 'sadf';
    if (part1 === 'xyz') {
        part2 = 'ert';
    } else {
        part2 = 'rld';
    }
    $scope.name = part1 + part2;

//    console.log("somedidlything")

    $http.get("http://localhost:8080/api/consumer")
        .then(function (response) {
            $scope.resp = response.data;
        });
});
