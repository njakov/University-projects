module bcd(
    input [5:0] in,
    output reg[3:0] ones,
    output reg [3:0] tens
   );
   
    integer i;
    reg [7:0] bcd;

    always @(in) begin
        bcd = 8'h00;		 	
        for (i=0; i<6; i=i+1) begin					//Iterate once for each bit in input number
            if (bcd[3:0] >= 5) bcd[3:0] = bcd[3:0] + 4'h3;		//If any BCD digit is >= 5, add three
            if (bcd[7:4] >= 5) bcd[7:4] = bcd[7:4] + 4'h3;
            bcd = {bcd[6:0], in[5-i]};				//Shift one bit, and shift in proper bit from input 
        end
        ones = bcd[3:0];
        tens = bcd[7:4];
    end
endmodule