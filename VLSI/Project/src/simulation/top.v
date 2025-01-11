module top; reg [2:0] dut_oc; reg [3:0] dut_a; reg [3:0] dut_b; wire [3:0] dut_f; alu aluDut(.oc(dut_oc), .a(dut_a), .b(dut_b), .f(dut_f));


reg dut_clk, dut_rst_n, dut_cl, dut_ld, dut_inc, dut_dec, dut_sr, dut_ir, dut_sl, dut_il;
reg [3:0] dut_in;
wire [3:0] dut_out;

register registerDut(
.clk(dut_clk),
.rst_n(dut_rst_n),
.cl(dut_cl),
.ld(dut_ld),
.in(dut_in),
.inc(dut_inc),
.dec(dut_dec),
.sr(dut_sr),
.ir(dut_ir),
.sl(dut_sl),
.il(dut_il),
.out(dut_out)
);


integer i;
initial begin
    
    for (i = 0; i < 2**11; i = i + 1) begin
        {dut_oc, dut_a, dut_b} = i;
        #5;
    end
    
    $stop;
    
    dut_rst_n                                                           = 1'b0;
    dut_clk                                                             = 1'b0;
    {dut_cl, dut_ld, dut_inc, dut_dec, dut_sr, dut_ir, dut_sl, dut_il } = 8'h00;
    dut_in                                                              = 4'b0000;
    
    #7 dut_rst_n = 1'b1;
    
    repeat (1000) begin
        {dut_cl, dut_ld, dut_inc, dut_dec, dut_sr, dut_ir, dut_sl, dut_il} = $urandom_range(255);
        dut_in                                                             = $urandom_range(15);
        #10;
    end
    
    $finish;
end


initial begin
    $monitor(
    "time = %4d, oc = %b, a = %d, b = %d, f = %d",
    $time, dut_oc, dut_a, dut_b, dut_f
    );
end

always @(dut_out)
    $strobe(
    "time = %4d, cl = %b, ld = %d, inc = %d, dec = %d, sr = %d, ir = %d, sl = %d, il = %d, out = %d",
    $time, dut_cl, dut_ld, dut_inc, dut_dec, dut_sr, dut_ir, dut_sl, dut_il, dut_out
    );

always
#5 dut_clk = ~dut_clk;

endmodule
