/*global angular*/    // Stop jsLint from complaining about globally defined variables.

var app = angular.module('govna', ['ui.bootstrap']);

app.controller('MainCtrl', function ($scope, $http) {
    'use strict';

    $scope.groupName = null;

    $scope.fetchGroup = function () {
        var uri = "http://localhost:8080/api/restrictions/group/" + $scope.groupName;
        console.log("Calling for group " + uri);
        $http.get(uri)
            .then(function (response) {
                $scope.groupRestrictions = response.data;
            });
    };

    $scope.clearGroup = function () {
        $scope.groupName = null;
        $scope.groupRestrictions = null;
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
