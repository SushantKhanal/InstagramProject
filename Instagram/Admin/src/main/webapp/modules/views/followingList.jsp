<div class="modal-header">
    <h1 class="modal-title" id="modal-title">Following list :</h1>
</div>

<div class="modal-body" id="modal-body">
    <div ng-repeat="follow in followingCtrl.following">
        <div class="col-md-12">
        <img class="img-responsive img-circle margin" src="/uploads/{{follow.image}}" width="10%" height="10%">
            {{follow.userName}}
        </div>

    </div>
</div>

<div class="modal-footer">
    <button class="btn btn-warning" type="button" ng-click="followingCtrl.ok()">Done</button>
</div>
