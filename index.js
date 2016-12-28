const fs = require('fs'),
	request = require('request'),
    async = require('async'),
	config = './config.json';


let r = null,
	conf = JSON.parse(fs.readFileSync(config, {encoding: 'utf8', flag: 'r'})),
	hosts = '',
	hostpath = conf.hostsPath,
    backupfileName = null,
    oldHosts = null,
	urls = conf.sourceList;

r = request.defaults({
	proxy: conf.proxy
});

backupOldHosts();
getAllHosts(urls);

/**
 * 备份现在的hosts到本目录
 */
function backupOldHosts() {
	console.log('正在备份原hosts文件...');
    oldHosts = fs.readFileSync(hostpath, {encoding: 'utf8'});
    backupfileName = 'hosts' + new Date().getTime();
    fs.writeFileSync(backupfileName, oldHosts);
    console.log('原hosts文件备份到！' + __dirname + '\\' + backupfileName);
    console.log('开始更新hosts文件，请耐心等待！');
}

/**
 * 循环获取所有的hosts
 * @param urls
 */
function getAllHosts(urls) {
    async.mapLimit(urls, 10, (item, cb) => {
        getHosts(item, cb);
    }, (err, result) => {
        console.log('hosts文件更新成功！');
        
    });
}

/**
 * 抓取hosts下的数据
 * @param url
 */
function getHosts(url, cb) {
    r.get(url)
        .on('error', (err) => {
            console.log(err)
            cb(err, url);
        })
        .on('data', (data) => {
            hosts += data;
        })
        .on('end', () => {
            fs.writeFile(hostpath, hosts, (err) => {
                if (err) {
                    cb(err, url);
                }
                cb(null, url)
            });
        })
}

