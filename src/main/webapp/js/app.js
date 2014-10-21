/*global angular*/    // Stop jsLint from complaining about globally defined variables.

angular
.module('govna', ['ui.bootstrap'])
.controller('MainCtrl', function ($scope, $http, $timeout) {
    'use strict';

    $scope.groupName = null;
    $scope.groupData = null;

    $scope.debugIsCollapsed = false;

    $scope.message = null;
    $scope.notFound = false;


    $scope.groupNameEnterKeyHit = function() {
        if ($scope.notFound) {
            $scope.newGroupData();
        }
        else {
            $scope.fetchGroup();
        }
    };

    var createExemptConsumersString = function() {
        if ($scope.groupData.restrictions === null) {
            $scope.groupData.restrictions = [];
        }
        $scope.groupData.restrictions.forEach(function (oneRestriction, index) {
            if (oneRestriction.exemptConsumers === null) {
                oneRestriction.exemptConsumers = [];
            }
            oneRestriction.exemptConsumersString = oneRestriction.exemptConsumers.join(', ');
        });
    };

///////////// Fetch group /////////////////
    $scope.fetchGroup = function () {
        $scope.clearStuff();
        if ($scope.groupName.length === 0) {
            $scope.clearGroup();
        }
        $scope.groupData = null;
        var uri = "http://localhost:8080/api/restrictions/group/" + $scope.groupName;
        console.log("Getting group " + uri);
        $http.get(uri)
            .then(handleGetGroup, handleGetGroupError);
    };

    var handleGetGroup = function(response) {
        var data = response.data;
        delete data._id;
        $scope.groupData = data;
        createExemptConsumersString();
    };

    var handleGetGroupError = function(reason) {
        if (reason.status === 404) {
            $scope.notFound = true;
        }
        else {
            $scope.message = "HTTP " + reason.status + " - " + reason.data;
        }
    };

/////////////// Save group //////////////////

    var fixRestrictions = function() {
        $scope.groupData.restrictions.forEach(function (oneRestriction) {
            // Replace all whitespace with an empty string
            oneRestriction.exemptConsumersString = oneRestriction.exemptConsumersString.replace(/\s/g, '');
            oneRestriction.exemptConsumers = oneRestriction.exemptConsumersString.split(',');
            delete oneRestriction.exemptConsumersString;  // delete from JSON going back to server.
        });
    };

    $scope.saveGroup = function() {
        fixRestrictions();
        $scope.saveGroupData();
    };

    $scope.saveGroupData = function() {
        var uri = "http://localhost:8080/api/restrictions/group/" + $scope.groupName;
        console.log("Putting group " + uri);
        $http.put(uri, $scope.groupData)
            .then(handlePutGroup, handlePutGroupError);
    };

    var handlePutGroup = function(response) {
        $scope.message = "Group " + $scope.groupData.groupName + " inserted/updated successfully.";
        // Put the whitespace back
        createExemptConsumersString();
    };

    var handlePutGroupError = function(reason) {
        $scope.message = "HTTP " + reason.status + " - " + reason.data;
    };

/////////////// Delete group ////////////////
    $scope.deleteGroupData = function() {
        var uri = "http://localhost:8080/api/restrictions/group/" + $scope.groupName;
        console.log("Deleting group " + uri);
        $http.delete(uri)
            .then(handleDeleteGroup, handleDeleteGroupError);    };

    var handleDeleteGroup = function(response) {
        var message = "Group " + $scope.groupData.groupName + " deleted successfully.";
        $scope.clearStuff();
        $scope.message = message;
    };

    var handleDeleteGroupError = function(reason) {
        if (reason.status === 404) {
            $scope.notFound = true;
        }
        else {
            $scope.message = "HTTP " + reason.status + " - " + reason.data;
        }
    };

////////////////////////////////////////////
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
        $scope.groupData = {groupName: $scope.groupName, restrictions: []};
        $scope.addElement();
    };

    $scope.addElement = function() {
        $scope.groupData.restrictions.push({"isDeprecated": false});
    };

    $scope.removeElement = function(index) {
        $scope.groupData.restrictions.splice(index, 1);
    };

});
