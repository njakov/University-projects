module clk_div #(
    parameter DIVISOR = 50000000

) (
input clk,
input rst_n,
output reg out

);
reg [31:0] count;
always @(posedge clk, negedge rst_n) begin
    if(!rst_n) begin
        count<=32'h0000;
        out<=1'b0;
    end 
    else begin 
        if(count==DIVISOR-1) begin
            count <= 32'h0000;
            out <= ~out;
        end 
        else begin 
            count <= count + 1 ;
        end 
    end 
end


endmodule