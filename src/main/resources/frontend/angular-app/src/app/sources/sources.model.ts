export class source{
    public id: number;
    public name: string;
    public type: string;
    public host: string;
    public port: number;
    public userName: string;
    public rootFolder: string;



	constructor(id: number, name: string, type: string, host: string, port: number, userName: string, rootFolder: string) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.rootFolder = rootFolder;
	}
	

}