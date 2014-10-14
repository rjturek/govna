/*global angular*/    // Stop jsLint from complaining about globally defined variables.

var app = angular.module('govna', ['ui.bootstrap']);

app.controller('MainCtrl', function ($scope, $http) {
    'use strict';

    $scope.groupName = null;
    $scope.groupData = null;

    $scope.fetchGroup = function () {
        if ($scope.groupName.length === 0) {
            $scope.clearGroup();
        }
        $scope.groupData = null;
        var uri = "http://localhost:8080/api/restrictions/group/" + $scope.groupName;
        console.log("Calling for group " + uri);
        $http.get(uri)
            .then(handleGroup, handleGroupError);
    };

    var handleGroup = function(response) {
        $scope.groupData = response.data;
    };

    var handleGroupError = function(reason) {
        if (reason.status === 404) {
            $scope.notFound = true;
        }
        else {
            $scope.errorMessage = "HTTP " + reason.status + " - " + reason.data;
        }
    };

    $scope.clearGroup = function () {
        $scope.groupName = null;
        $scope.groupData = null;
        $scope.errorMessage = null;
        $scope.notFound = false;
    };

    $scope.newGroupData = function() {
        $scope.notFound = false;
        $scope.groupData = {groupName: $scope.groupName};
    };

    $scope.clearStuff = function() {
        $scope.errorMessage = null;
        $scope.notFound = false;
    };

    $scope.saveGroupData = function() {
        $scope.errorMessage = "saving now";
    };

    $scope.artifactIsCollapsed = true;
    $scope.versionIsCollapsed = true;
    $scope.artifactVersionIsCollapsed = true;

    $scope.errorMessage = null;
    $scope.notFound = false;

});
