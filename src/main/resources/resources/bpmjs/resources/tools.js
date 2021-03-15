import $ from 'jquery'

const proHost = window.location.protocol + '//' + window.location.host;
const href = window.location.href.split('bpmjs')[0];
const key = href.split(window.location.host)[1];
const publicurl = proHost + key;
const tools = {
    /*
    下载bpmn
     */
    download(bpmnModeler) {
        var downloadLink = $('#downloadLoadBPMN')
        bpmnModeler.saveXML({format: true}, function (err, xml) {
            if (err) {
                return console.error('cloud not save bpmn', err)
            }
            tools.setEncoded(downloadLink, 'diagram.bpmn', err ? null : xml)
        });
    },
    /*
    将 编写好的Bpmn存入数据库
     */
    saveBPMN(bpmnModeler) {
        bpmnModeler.saveXML({format: true}, function (err, xml) {
            if (err) {
                return console.error('cloud not save bpmn', err)
            }
            var param = {
                'multipartFile': xml
            }
            $.ajax({
                url: publicurl + 'processDefinition/addDeploymentByString',
                // url: 'localhost:89/processDefinition/addDeploymentByString',
                type: 'POST',
                datatype: 'json',
                data: param,
                success: function (result) {
                    if (result.status == '0') {
                        alert('BPMN部署成功');
                    } else {
                        alert(result.msg.toString());
                    }
                },
                error: function (err) {
                    alert(err)
                }
            })
        });
    },
    /*
    上传流程文件
     */
    uploadBPMN(bpmnModeler) {
        var FileUpload = document.myForm.uploadFile.files[0];
        var fm = new FormData();
        fm.append('processFile', FileUpload);
        $.ajax({
            url: publicurl + 'processDefinition/addDeploymentByString',
            type: 'POST',
            datatype: 'json',
            data: param,
            success: function (result) {
                if (result.status == '0') {
                    alert('BPMN部署成功');
                } else {
                    alert(result.msg);
                }
            },
            error: function (err) {
                alert(err)
            }
        })
    },
    setEncoded(link, name, data) {
        var encodedData = encodeURIComponent(data);

        if (data) {
            link.addClass('active').attr({
                'href': 'data:application/bpmn20-xml;charset=UTF-8,' + encodedData,
                'download': name
            });
        } else {
            link.removeClass('active');
        }
    }
}
//给文件命名
export default tools