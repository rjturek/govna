/*global angular*/    // Stop jsLint from complaining about globally defined variables.

angular
    .module('govna', ['ui.bootstrap'])
    .controller('MainCtrl', function ($scope, $http, $timeout) {
    'use strict';

    $scope.groupName = null;
    $scope.groupData = null;

    $scope.debugIsCollapsed = false;

    $scope.groupIsCollapsed = true;
    $scope.artifactIsCollapsed = true;
    $scope.versionIsCollapsed = true;
    $scope.artifactVersionIsCollapsed = true;

    $scope.message = null;
    $scope.notFound = false;

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
        var data = response.data;
        $scope.groupData = data;
        if (data.restriction) {
            $scope.groupIsCollapsed = false;
        }
        if (data.artifactRestrictions) {
            $scope.artifactIsCollapsed = false;
        }
        if (data.versionRestrictions) {
            $scope.versionIsCollapsed = false;
        }
        if (data.artifactVersionRestrictions) {
            $scope.artifactVersionIsCollapsed = false;
        }
    };

    var handleGroupError = function(reason) {
        if (reason.status === 404) {
            $scope.notFound = true;
        }
        else {
            $scope.message = "HTTP " + reason.status + " - " + reason.data;
        }
    };

    $scope.alrt = function() {
        alert("HHHHHHHHHHHHHHHey");
    };

    $scope.clearGroup = function () {
        $scope.groupName = null;
        $scope.groupData = null;
        $scope.message = null;
        $scope.notFound = false;
    };

    $scope.clearStuff = function () {
        $scope.groupData = null;
        $scope.message = null;
        $scope.notFound = false;
    };

    $scope.newGroupData = function() {
        $scope.notFound = false;
        $scope.groupData = {groupName: $scope.groupName};
    };

    $scope.saveGroupData = function() {
        $scope.message = "saving now";
    };

    $scope.deleteGroupData = function() {
        $scope.message = "deleting now";
    };

    $scope.addVersion = function(msg) {
        $scope.message = msg + " clicked ";
        if (!$scope.groupData.versionRestrictions) {
            $scope.groupData.versionRestrictions = [];
        }
//        $scope.groupData.versionRestrictions.push(msg);
        $scope.groupData.versionRestrictions.splice(0,0,msg);
    };

});
