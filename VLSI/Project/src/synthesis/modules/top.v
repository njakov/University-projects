module top #(parameter DIVISOR=50000000, parameter FILE_NAME="mem_init.mif",
             parameter ADDR_WIDTH=6, parameter DATA_WIDTH=16) (
    input clk,
    input rst_n,
    input[2:0] btn,
    input [8:0] sw,
    output [9:0] led,
    output [27:0] hex
);
    //////////////NEW CLOCK
    //wire rst_n;
    //assign rst_n = sw[9];
    
    wire clk_slowed;
    clk_div #(DIVISOR) clk_div_inst(.clk(clk), .rst_n(rst_n), .out(clk_slowed));

    ///////MEMORY////////////
    wire mem_we;
    wire [ADDR_WIDTH-1:0] mem_addr;
    wire [DATA_WIDTH-1:0] mem_data;
    wire [DATA_WIDTH-1:0] mem_out;

    wire [ADDR_WIDTH-1:0] pc, sp;
    wire [DATA_WIDTH-1:0] proc_out;

    memory MEMORY(.clk(clk_slowed), .we(mem_we), .addr(mem_addr), .data(mem_data), .out(mem_out));
    
    assign led[4:0] = proc_out[4:0];


    wire [3:0] sp_tens, sp_ones;
    wire [3:0] pc_tens, pc_ones;

    bcd bcd_sp(.in(sp), .tens(sp_tens), .ones(sp_ones));
    bcd bcd_pc(.in(pc), .tens(pc_tens), .ones(pc_ones));

    ssd ssd_sp_tens (.in(sp_tens), .out(hex[27:21]));
    ssd ssd_sp_ones (.in(sp_ones), .out(hex[20:14]));
    ssd ssd_pc_tens (.in(pc_tens), .out(hex[13:7]));
    ssd ssd_pc_ones (.in(pc_ones), .out(hex[6:0]));


    cpu #(ADDR_WIDTH, DATA_WIDTH) cpu_inst (
        .clk(clk_slowed),
        .rst_n(rst_n),
        .mem_in(mem_out),
        .in({{(DATA_WIDTH-4){1'b0}},{sw[3:0]}}),
        .mem_we(mem_we),
        .mem_addr(mem_addr),
        .mem_data(mem_data),
        .out(proc_out),
        .pc(pc),
        .sp(sp)
    );
    
endmodule