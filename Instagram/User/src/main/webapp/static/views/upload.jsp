<div class="modal-header">
    <h3 class="modal-title" id="modal-title">Upload Photo</h3>
</div>
<div class="modal-body" id="modal-body">

<form>
    <%--<input type = "text" name="caption" ng-model ="img.caption" placeholder="Write a caption...">--%>
    <input type="file" ng-model="img.listOfImages" name="files" multiple accept="image/*, .zip"
           maxsize="5000" required base-sixty-four-input>
</form>

    <div ng-show="img.showCaptionOptions">
    <div ng-repeat="i in img.listOfImages" class = "col-lg-4">
        <img src="data:image/jpeg;base64,{{i.base64}}" width="100px" height="100px">

        <input type = "text" name="caption" ng-model ="i.listOfImages.caption" placeholder="Write a caption...">
    </div>
    </div>

</div>

<div class="modal-footer">
    <%--<button class="btn btn-primary" type="button" ng-click="img.uploadPhoto()" ng-disabled="img.submitClicked">Upload photo</button>--%>
    <button class="btn btn-primary" type="button" ng-click="img.showCaptionAndSelectedPhoto()" ng-disabled="img.submitClicked">Upload photo</button>
    <button class="btn btn-warning" type="button" ng-click="img.close()">Cancel</button>
</div>