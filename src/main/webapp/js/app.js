/*global angular*/    // Stop jsLint from complaining about globally defined variables.

var app = angular.module('govna', ['ui.bootstrap']);

app.controller('MainCtrl', function ($scope, $http) {
    'use strict';

    $scope.groupName = null;
    $scope.groupRestrictions = null;

    $scope.fetchGroup = function () {
        $scope.groupRestrictions = null;
        var uri = "http://localhost:8080/api/restrictions/group/" + $scope.groupName;
        console.log("Calling for group " + uri);
        $http.get(uri)
            .then(handleGroup, handleGroupError);
    };

    var handleGroup = function(response) {
        $scope.groupRestrictions = response.data;
    };

    var handleGroupError = function(reason) {
        if (reason.status === 404) {
            $scope.messageText = "No group found for " + $scope.groupName;
        }
        else {
            $scope.messageText = "HTTP " + reason.status + " - " + reason.data;
        }
    };

    $scope.clearGroup = function () {
        $scope.groupName = null;
        $scope.groupRestrictions = null;
        $scope.messageText = null;
    };

    $scope.artifactIsCollapsed = true;
    $scope.versionIsCollapsed = true;
    $scope.artifactVersionIsCollapsed = true;

    $scope.messageText = null;

});
