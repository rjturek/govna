/*global angular*/    // Stop jsLint from complaining about globally defined variables.

var app = angular.module('govna', ['ui.bootstrap']);

app.controller('MainCtrl', function ($scope, $http) {
    'use strict';
    var part1 = 'Wo';
    var part2 = 'rld';
    $scope.name = part1 + part2;

//    console.log("Calling api/consumer")
//    $http.get("http://localhost:8080/api/consumer")
//        .then(function (response) {
//            $scope.resp = response.data;
//        });

    //alert("hey");

    $scope.buttonClicked = function () {
        alert("you clicked me");
    };

    $scope.alerts = [
        { type: 'danger', msg: 'Oh snap! Change a few things up and try submitting again.' },
        { type: 'success', msg: 'Well done! You successfully read this important alert message.' }
    ];

    $scope.addAlert = function() {
        $scope.alerts.push({msg: 'Another alert!'});
    };

    $scope.closeAlert = function(index) {
        $scope.alerts.splice(index, 1);
    };

});
