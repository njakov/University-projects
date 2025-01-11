module register(clk,
                rst_n,
                cl,
                ld,
                in,
                inc,
                dec,
                sr,
                ir,
                sl,
                il,
                out);
    
    input clk, rst_n, cl, ld, inc, dec, sr, sl;
    input ir, il;
    
    input [3:0] in;
    output reg [3:0] out;
    
    assign data_out = out;
    always @(posedge clk, negedge rst_n) begin
        if (!rst_n) begin
            out <= 8'h00;
            end else begin
            if (cl)
                out <= {4{1'b0}};
            else if (ld)
                out <= in;
            else if (inc)
                out <= out + {{3{1'b0}}, 1'b1};
            else if (dec)
                out <= out - {{3{1'b0}}, 1'b1};
            else if (sr)
                out <= {ir, out[3:1]};
            else if (sl)
                out <= {out[2:0], il};
                end
                end
                endmodule
