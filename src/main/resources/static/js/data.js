

$(() => {
    const charts = {
        dau: echarts.init(document.getElementById('dau-chart'),'macarons'),
        uv: echarts.init(document.getElementById('uv-chart'),'macarons'),
    };

    initChart('uv');
    initChart('dau');

    $(`#uv-submit`).click(count('uv'));
    $(`#dau-submit`).click(count('dau'));


    initData('uv');
    setTimeout(() => initData('dau'), 100);

    function count(type) {
        type = type.toString().toLowerCase();
        if (type !== 'uv' && type !== 'dau') {
            throw '无法计算' + type;
        }
        const url = CONTEXT_PATH + "/data/" + type;
        return function () {
            const start = $(`#${type}-start`).val();
            const end = $(`#${type}-end`).val();
            charts[type].showLoading({
                text: 'loading',
                color: '#4cbbff',
                textColor: '#4cbbff',
                maskColor: 'rgba(255, 255, 255, 0.1)',
            });
            $.post(url, { start, end }, data => {
                data = JSON.parse(data);
                if (data.code === 0) {
                    $(`#${type}-total`).text(data.count);
                    fillChart(type, data.list);
                    charts[type].hideLoading();
                } else {
                    alert(data.msg);
                }
            })
            return false;
        }
    }

    function initChart(type) {
        type = type.toString().toLowerCase();
        if (type !== 'uv' && type !== 'dau') {
            throw '无法计算' + type;
        }
        let typeName = type === 'uv' ? '访客' : '活跃用户';
        charts[type].hideLoading();
        charts[type].setOption({
            title: {
                text: typeName,
            },
            tooltip: {
                trigger: 'axis',
            },
            toolbox: {
                show: true,
                feature: {
                    dataView: {
                        readOnly: false
                    }, //数据视图
                    magicType: {
                        type: ['line', 'bar']
                    }, //切换为折线图，切换为柱状图
                }
            },
            legend: {
                data:[typeName]
            },
            xAxis: {
                data: []
            },
            yAxis: {},
            series: [{
                name: typeName,
                type: 'bar',
                data: [],
                smooth: true,
            }],
            grid: {
                top: '15%',
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
        });
    }

    function fillChart(type, list) {
        type = type.toString().toLowerCase();
        if (type !== 'uv' && type !== 'dau') {
            throw '无法计算' + type;
        }
        let typeName = type === 'uv' ? '访客' : '活跃用户';
        let axis = [];
        let datas = [];

        list.forEach(i => {
            axis.push(i.date);
            datas.push(i.data);
        })

        charts[type].setOption({
            xAxis: {
                data: axis
            },
            series: [{
                name: typeName,
                data: datas
            }]
        })
    }

    function formatDate(date) {
        let d = new Date(date),
            month = '' + (d.getMonth() + 1),
            day = '' + d.getDate(),
            year = d.getFullYear();

        if (month.length < 2) month = '0' + month;
        if (day.length < 2) day = '0' + day;

        return [year, month, day].join('-');
    }

    function initData(type) {
        let today = new Date();
        let sevenDayAgo = new Date(today.valueOf() - 1000 * 60 * 60 * 24 * 7); // 7天前

        $(`#${type}-start`).val(formatDate(sevenDayAgo));
        $(`#${type}-end`).val(formatDate(today));

        $(`#${type}-submit`).click();
    }
});