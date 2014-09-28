var app = angular.module('govna', []);

app.controller('MainCtrl', function($scope) {
    var part1 = 'Wo'
    var part2
    if (part1 == 'xyz') {
        part2 = 'ert'
    }
    else {
        part2 = 'rld'
    }
    $scope.name = part1 + part2;
});
