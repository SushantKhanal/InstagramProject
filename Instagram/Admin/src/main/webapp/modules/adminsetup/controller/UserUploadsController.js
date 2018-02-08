(function () {
    angular.module("adminModule").controller("UserUploadsController", UserUploadsController);

    UserUploadsController.$inject = ['HttpService', '$localStorage','$log'];

    function UserUploadsController(HttpService, $localStorage,$log) {
        var vm = this;
        vm.uploadList = [];
        vm.showing = false;
        vm.showList = true;

        vm.totalItems = '';
        vm.CurrentPage =1;
        vm.maxSize = 2;
        vm.pageChanged= pageChanged;

        vm.getUploadsOfUser = getUploadsOfUser;
        vm.showComments = showComments;
        vm.openLikeListModal=openLikeListModal;
        getUploadsOfUser();

        function getUploadsOfUser() {
            var URL = "/getUploadsOf/"+$localStorage.showUploadsOfUser+"?page="+vm.CurrentPage+"&size="+vm.maxSize;
            HttpService.get(URL).then(
                function (value) {
                    vm.uploadList = value;
                    vm.totalItems = value[0].totalItems;
                }, function (reason) {
                    console.log(reason);
                });
        }

        function showComments(uploads) {
            HttpService.get("/getCommentsOfThisPicture/" + uploads.image_path).then(
                function (value) {
                    if (vm.showing) {
                        vm.showList = false;
                        vm.showing = false;
                    } else {
                        vm.commentList = value;
                        vm.showing = true;
                    }
                }, function (reason) {
                    console.log(reason);
                });
        }

        function pageChanged() {
            $log.log("Page changed to:"+vm.CurrentPage);
            if (vm.CurrentPage < (vm.totalItems/vm.maxSize)) {
                vm.CurrentPage += 1;
            }
            getUploadsOfUser();
        };

        function openLikeListModal(post) {
            $rootScope.imageName = post.image_path;
            vm.modalInstance=$uibModal.open({
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/static/views/likesList.jsp',
                controller :'LikesListController',
                controllerAs: 'likesctrl',
                size: 'lg'
            });
        }

    }
})();
