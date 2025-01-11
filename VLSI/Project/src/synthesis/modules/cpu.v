module cpu #(
    parameter ADDR_WIDTH = 6,
    parameter DATA_WIDTH = 16
)(
    input clk,
    input rst_n,
    input [DATA_WIDTH - 1:0] mem_in,
    input [DATA_WIDTH - 1:0] in,

    output reg mem_we,
    output reg [ADDR_WIDTH - 1:0] mem_addr,
    output reg [DATA_WIDTH - 1:0] mem_data,
    output [DATA_WIDTH - 1:0] out,

    output [ADDR_WIDTH - 1:0] pc,
    output [ADDR_WIDTH - 1:0] sp
);


    /////////////// PC, SP, IR_HIGH, IR_LOW, ALU

    reg pc_ld, pc_inc;
    reg[ADDR_WIDTH-1:0] pc_in;


    register #(
        .DATA_WIDTH(6)
    ) program_counter (
        .rst_n(rst_n),
        .clk(clk),
        .cl(1'b0),
        .ld(pc_ld),
        .in(pc_in),
        .inc(pc_inc),
        .dec(1'b0),
        .sr(1'b0),
        .ir(1'b0),
        .sl(1'b0),
        .il(1'b0),
        .out(pc)
    );
    
    reg sp_ld, sp_inc; //zameni sa 0
    reg[ADDR_WIDTH-1:0] sp_in;

    register #(
        .DATA_WIDTH(6)
    ) stack_pointer (
        .rst_n(rst_n),
        .clk(clk),
        .cl(1'b0),
        .ld(sp_ld),
        .in(sp_in),
        .inc(sp_inc),
        .dec(1'b0),
        .sr(1'b0),
        .ir(1'b0),
        .sl(1'b0),
        .il(1'b0),
        .out(sp)
    );


    reg irhigh_ld, irhigh_inc;

    wire [DATA_WIDTH-1:0] irhigh_in;
    assign irhigh_in = mem_in;

    wire [DATA_WIDTH - 1:0] ir_high; //ovde ide prvi bajt instrukcije

    register #(
        .DATA_WIDTH(16)
    ) irhigh (
        .rst_n(rst_n),
        .clk(clk),
        .cl(1'b0),
        .ld(irhigh_ld),
        .in(irhigh_in),
        .inc(irhigh_inc),
        .dec(1'b0),
        .sr(1'b0),
        .ir(1'b0),
        .sl(1'b0),
        .il(1'b0),
        .out(ir_high)
    );

    reg irlow_ld, irlow_inc;

    wire [DATA_WIDTH-1:0] irlow_in;
    assign irlow_in = mem_in;

    wire [DATA_WIDTH - 1:0] ir_low; //ovde ide druga rec po potrebi tipa za poseban mov

    register #(
        .DATA_WIDTH(16)
    ) ir_ow (
        .rst_n(rst_n),
        .clk(clk),
        .cl(1'b0),
        .ld(irlow_ld),
        .in(irlow_in),
        .inc(irlow_inc),
        .dec(1'b0),
        .sr(1'b0),
        .ir(1'b0),
        .sl(1'b0),
        .il(1'b0),
        .out(ir_low)
    );

    reg alu_ld;
    reg [DATA_WIDTH-1:0] alu_in;
    wire [DATA_WIDTH - 1:0] alu;

    register #(
        .DATA_WIDTH(16)
    ) alu_register (
        .rst_n(rst_n),
        .clk(clk),
        .cl(1'b0),
        .ld(alu_ld),
        .in(alu_in),
        .inc(1'b0),
        .dec(1'b0),
        .sr(1'b0),
        .ir(1'b0),
        .sl(1'b0),
        .il(1'b0),
        .out(alu)
    );

    reg [DATA_WIDTH-1:0] alu_a;
    reg [DATA_WIDTH-1:0] alu_b;
    wire [DATA_WIDTH-1:0] alu_f;
    reg [2:0] alu_oc;

    alu #(
        .DATA_WIDTH(16)
    ) alu_unit (
        .oc(alu_oc),
        .a(alu_a),
        .b(alu_b),
        .f(alu_f)
    );


    /////////////kombinacioni signali 
    //reg mem_we;
    //reg [5:0] mem_addr, mem_data;

    ////////////////////registri sekvencijalne logike

    
    // Instructions
    localparam MOV = 4'b0000;
    localparam IN = 4'b0111;
    localparam OUT = 4'b1000;
    localparam ADD = 4'b0001;
    localparam SUB = 4'b0010;
    localparam MUL = 4'b0011;
    localparam DIV = 4'b0100;
    localparam STOP = 4'b1111;

    //States 
    localparam SETUP = 3'b000;
    localparam FETCH = 3'b001;
    localparam DECODE = 3'b010;
    localparam EXEC1 = 3'b011;
    localparam EXEC2 = 3'b100;
    //localparam EXEC3 = 3'b101;
    localparam FINISH = 3'b111;

    reg [2:0] state_reg, state_next;

    //Output - sekvencijalna logika
    reg [DATA_WIDTH - 1:0] out_reg, out_next;
    assign out = out_reg;
        
    //Local
    integer cnt_reg, cnt_next;

    //Sekvencijalna logika, radimo samo sa internim registrima
    always @(posedge clk or negedge rst_n) begin
        if (!rst_n) begin
            state_reg <= SETUP;
            cnt_reg <= 0;
            out_reg <= 16'h0000;
        end else begin
            cnt_reg <= cnt_next;
            state_reg <= state_next;
            out_reg <= out_next;
        end
    end

    //Combinational

    always @(*) begin

        {pc_ld, pc_inc, sp_ld, sp_inc} = 4'h0;
        {irhigh_ld, irhigh_inc, irlow_ld, irlow_inc} = 4'h0;

        {alu_ld, alu_oc} = 4'h0;
        alu_a = {(DATA_WIDTH){1'b0}};
        alu_b = {(DATA_WIDTH){1'b0}};
        alu_in = {(ADDR_WIDTH){1'b0}};
        sp_in = {(ADDR_WIDTH){1'b0}};

        mem_we = 1'b0;
        mem_addr = {(ADDR_WIDTH){1'b0}};
        mem_data = {(DATA_WIDTH){1'b0}};
        pc_in =  {(ADDR_WIDTH){1'b0}};
        //aluin je pcin
        cnt_next = cnt_reg;
        state_next = state_reg;

        out_next = out_reg;

        //out_next = state_reg;
        case (state_reg)
            SETUP: begin
                pc_in = {{(ADDR_WIDTH-4){1'b0}}, 4'b1000}; // Concatenate zeroes with the desired value
                pc_ld = 1'b1;
                sp_in = {(ADDR_WIDTH){1'b1}};
                sp_ld = 1'b1;
                state_next = FETCH;
                cnt_next = 0;                 
            end
            FETCH: begin
                // Fetch instruction from memory
                //out_next = 1'b1;
                
                sp_in = {(ADDR_WIDTH){1'b0}}; //provera da li udje u fetch
                sp_ld = 1'b1; //

                if (cnt_next == 0) begin
                    mem_addr = pc;
                    mem_we = 1'b0;
                    pc_inc = 1'b1;
                    cnt_next = cnt_reg + 1; 

                    state_next = FETCH;
                    
                end else if (cnt_next == 1) begin
                   
                    mem_addr = pc;
                    mem_we = 1'b0;
                    irhigh_ld = 1'b1;
                    cnt_next = cnt_reg + 1; 

                    state_next = FETCH;
                    
                end else if (cnt_next == 2) begin
                    //razlikaaaaa
                    if (ir_high[DATA_WIDTH-1:DATA_WIDTH-4] == MOV && ir_high[3:0] == 4'b1000) begin
                        mem_addr = pc;
                        mem_we = 1'b0;
                        irlow_ld = 1'b1;
                        state_next = FETCH;
                        cnt_next = cnt_reg + 1;
                        pc_inc =  1'b1;
                    end else begin 
                        //pc_inc = 1'b1;
                        state_next = DECODE;
                        cnt_next = 4'h0;
                    end
                end
                else if (cnt_next == 3) begin
                    //pc_inc = 1'b1;
                    state_next = DECODE;
                    cnt_next = 0;
                end      
            end
            DECODE: begin
                case(ir_high[15:12])
                    MOV: begin 
                        if (ir_high[3:0] == 4'b1000) begin
                            alu_in = ir_low;
                            alu_ld = 1'b1;
                            state_next = EXEC1;
                            cnt_next = 0;
                        end else if(ir_high[3:0] == 4'b0000) begin 
                            if (ir_high[7] == 0) begin 
                                //direktno
                                if (cnt_next == 0) begin 
                                    mem_addr = ir_high[6:4];
                                    mem_we = 1'b0;
                                    state_next = DECODE;
                                    cnt_next = cnt_next + 1;
                                end else if (cnt_next == 1) begin 
                                    alu_in = mem_in;
                                    alu_ld = 1'b1;
                                    state_next = EXEC1;
                                    cnt_next = 0;   
                                end
                            end
                        end else if (ir_high[7] == 1) begin
                            //indirektno
                            if (cnt_next == 0) begin
                                //procitati adresu iz memorije
                                mem_addr = ir_high[6:4];
                                mem_we = 1'b0;
                                state_next = DECODE;
                                cnt_next = cnt_next + 1;

                            end else if (cnt_next == 1) begin
                                //procitati vrednost iz memorije sa nove adrese
                                mem_addr = mem_in[ADDR_WIDTH-1:0];
                                mem_we = 1'b0;
                                state_next = DECODE;
                                cnt_next = cnt_next + 1;

                            end else if (cnt_next == 2) begin
                                alu_in = mem_in;
                                alu_ld = 1'b1;
                                state_next = EXEC1;
                                cnt_next = 0;
                            end
                        end
                    end     

                    IN: begin
                        if (ir_high[11] == 0) begin 
                            //direktno
                            mem_addr = ir_high[10:8];
                            mem_we = 1'b1;
                            mem_data = in;
                            state_next = FETCH;
                        end else if (ir_high[11] == 1) begin 
                            //indirektno
                            if (cnt_next == 0) begin
                                //dva takta, procita adresa iz memorije citanje + upis
                                mem_addr = ir_high[10:8];
                                mem_we = 1'b0;
                                state_next = DECODE;
                                cnt_next = cnt_next + 1;
                            end else if (cnt_next == 1) begin
                                mem_addr = mem_in[ADDR_WIDTH-1:0];
                                mem_we = 1'b1;
                                mem_data = in;
                                state_next = DECODE;
                                cnt_next = cnt_next + 1;
                            end else if (cnt_next == 2) begin
                                state_next = FETCH;
                                cnt_next = 0;
                            end
                        end
                    end

                    OUT: begin
                        if (ir_high[11] == 0) begin 
                            //direktno
                            if (cnt_next == 0) begin 
                                mem_addr = ir_high[10:8];
                                mem_we = 1'b0;
                                state_next = DECODE;
                                cnt_next = cnt_next + 1;
                            end else if (cnt_next == 1) begin 
                                out_next = mem_in;
                                state_next = FETCH;
                                cnt_next = 0;
                            end
                        end else if (ir_high[11] == 1) begin
                            //indirektno
                            if (cnt_next == 0) begin
                                //procitati adresu iz memorije
                                mem_addr = ir_high[10:8];
                                mem_we = 1'b0;
                                state_next = DECODE;
                                cnt_next = cnt_next + 1;

                            end else if (cnt_next == 1) begin
                                //procitati vrednost iz memorije sa nove adrese
                                mem_addr = mem_in[ADDR_WIDTH-1:0];
                                mem_we = 1'b0;
                                state_next = DECODE;
                                cnt_next = cnt_next + 1;

                            end else if (cnt_next == 2) begin
                                //poslati na out
                                out_next = mem_in;
                                state_next = FETCH;
                                cnt_next = 0;
                            end
                        end
                    end

                    ADD, SUB, MUL, DIV: begin

                        if (ir_high[7] == 0) begin 
                            //direktno
                            if (cnt_next == 0) begin 
                                mem_addr = ir_high[6:4];
                                mem_we = 1'b0;
                                state_next = DECODE;
                                cnt_next = cnt_next + 1;
                            end else if (cnt_next == 1) begin 
                                //spremljen je samo A 
                                //u exec spremamo B tj citamo sa adrese gde je B pa 
                                alu_in = mem_in;
                                alu_ld = 1'b1;
                                state_next = EXEC1;
                                cnt_next = 0;
                            end
                        end else if (ir_high[7] == 1) begin
                            //indirektno
                            if (cnt_next == 0) begin
                                //procitati adresu iz memorije
                                mem_addr = ir_high[6:4];
                                mem_we = 1'b0;
                                state_next = DECODE;
                                cnt_next = cnt_next + 1;

                            end else if (cnt_next == 1) begin
                                //procitati vrednost iz memorije sa nove adrese
                                mem_addr = mem_in[ADDR_WIDTH-1:0];
                                mem_we = 1'b0;
                                state_next = DECODE;
                                cnt_next = cnt_next + 1;

                            end else if (cnt_next == 2) begin
                                //poslati na alu
                                alu_in = mem_in;
                                alu_ld = 1'b1;
                                state_next = EXEC1;
                                cnt_next = 0;
                            end
                        end
                        
                    end
              

                    STOP: begin
                        if (ir_high[10:8] != 4'b000) begin
                            //direktno
                            if (ir_high[11] == 0) begin

                                if (cnt_next == 0) begin 
                                    mem_addr = ir_high[10:8];
                                    mem_we = 1'b0;
                                    state_next = DECODE;
                                    cnt_next = cnt_next + 1;
                                end else if (cnt_next == 1) begin 
                                    //provera da li je 0000 operand
                                    out_next = mem_in; 
                                    state_next = EXEC1;
                                    cnt_next = 0;
                                end

                            end else if (ir_high[11] == 1) begin
                                //idirirektno
                                if (cnt_next == 0) begin
                                    //procitati adresu iz memorije
                                    mem_addr = ir_high[10:8];
                                    mem_we = 1'b0;
                                    state_next = DECODE;
                                    cnt_next = cnt_next + 1;
                                end else if (cnt_next == 1) begin
                                    //procitati vrednost iz memorije sa nove adrese
                                    mem_addr = mem_in[ADDR_WIDTH-1:0];
                                    mem_we = 1'b0;
                                    state_next = DECODE;
                                    cnt_next = cnt_next + 1;
                                end else if (cnt_next == 2) begin
                                    //poslati na out
                                    out_next = mem_in; 
                                    state_next = EXEC1;
                                    cnt_next = 0;
                                end
                            end

                        end else begin
                            state_next = EXEC1;
                            cnt_next = 0; 
                        end
                    end
                endcase //kraj decode case
            end //kraj decode faze
            
            EXEC1: begin
                case(ir_high[15:12])

                    MOV: begin
                        if (ir_high[11] == 0) begin 
                            //direktno
                            mem_addr = ir_high[10:8];
                            mem_we = 1'b1;
                            mem_data = alu;
                            state_next = FETCH;
                            cnt_next = 0;
                        end else if (ir_high[11] == 1) begin 
                            //indirektno
                            if (cnt_next == 0) begin
                                //dva takta, procita adresa iz memorije citanje + upis
                                mem_addr = ir_high[10:8];
                                mem_we = 1'b0;
                                state_next = EXEC1;
                                cnt_next = cnt_next + 1;
                            end else if (cnt_next == 1) begin
                                mem_addr = mem_in[ADDR_WIDTH-1:0];
                                mem_we = 1'b1;
                                mem_data = alu;
                                state_next = EXEC1;
                                cnt_next = cnt_next + 1;
                            end else if (cnt_next == 2) begin
                                state_next = FETCH;
                                cnt_next = 0;
                            end
                        end
                    end
                    
                    ADD, SUB, MUL, DIV: begin 

                        //U ALU NAM JE A moramo da vidimo B operand gde je 
                        if (ir_high[3] == 0) begin 
                            //direktno
                            if (cnt_next == 0) begin 
                                mem_addr = ir_high[2:0];
                                mem_we = 1'b0;
                                state_next = EXEC1;
                                cnt_next = cnt_next + 1;
                            end else if (cnt_next == 1) begin 
                                //spremljen je B
                                alu_a = alu;
                                alu_b = mem_in;
                                alu_oc = ir_high[14:12] - 1'b1;
                                alu_in = alu_f;
                                alu_ld = 1'b1;
                                //rezultat sacuvam u akumulatoru a+b 
                                state_next = EXEC2;
                                cnt_next = 0;
                            end
                        end else if (ir_high[3] == 1) begin
                            //indirektno
                            if (cnt_next == 0) begin
                                //procitati adresu iz memorije
                                mem_addr = ir_high[2:0];
                                mem_we = 1'b0;
                                state_next = EXEC1;
                                cnt_next = cnt_next + 1;

                            end else if (cnt_next == 1) begin
                                //procitati vrednost iz memorije sa nove adrese
                                mem_addr = mem_in[ADDR_WIDTH-1:0];
                                mem_we = 1'b0;
                                state_next = EXEC1;
                                cnt_next = cnt_next + 1;

                            end else if (cnt_next == 2) begin
                                //spremljen je B
                                alu_a = alu;
                                alu_b = mem_in;
                                alu_oc = ir_high[14:12] - 1'b1;
                                alu_in = alu_f; ///ajde
                                alu_ld = 1'b1;
                                state_next = EXEC2;
                                cnt_next = 0;
                            end
                        end
                    end
                   
                    STOP: begin
                        if (ir_high[6:4]!= 4'b0000) begin 

                            if (ir_high[7] == 0) begin 
                                //direktno
                                if (cnt_next == 0) begin 
                                    mem_addr = ir_high[6:4];
                                    mem_we = 1'b0;
                                    state_next = EXEC1;
                                    cnt_next = cnt_next + 1;
                                end else if (cnt_next == 1) begin 
                                    //provera da li je 0000 operand
                                    out_next = mem_in; 
                                    state_next = EXEC2;
                                    cnt_next = 0;
                                end
                            end else if (ir_high[7] == 1) begin
                                //indirektno
                                if (cnt_next == 0) begin
                                    //procitati adresu iz memorije
                                    mem_addr = ir_high[6:4];
                                    mem_we = 1'b0;
                                    state_next = EXEC1;
                                    cnt_next = cnt_next + 1;

                                end else if (cnt_next == 1) begin
                                    //procitati vrednost iz memorije sa nove adrese
                                    mem_addr = mem_in[ADDR_WIDTH-1:0];
                                    mem_we = 1'b0;
                                    state_next = EXEC1;
                                    cnt_next = cnt_next + 1;

                                end else if (cnt_next == 2) begin
                                    //poslati na out
                                    out_next = mem_in; 
                                    state_next = EXEC2;
                                    cnt_next = 0;
                                end
                            end
                    end else begin
                        state_next = EXEC2;
                        cnt_next = 0;
                    end
                    end
                endcase //end EXEC1 case
            end //end EXEC1

            EXEC2: begin
                case(ir_high[15:12])
                    
                    ADD, SUB, MUL, DIV: begin
                        if (ir_high[11] == 0) begin 
                            //direktno
                            mem_addr = ir_high[10:8];
                            mem_we = 1'b1;
                            mem_data = alu;
                            state_next = FETCH;
                            //cnt_next = 0;
                        end else if (ir_high[11] == 1) begin 
                            //indirektno
                            if (cnt_next == 0) begin
                                mem_addr = ir_high[10:8];
                                mem_we = 1'b0;
                                state_next = EXEC2;
                                cnt_next = cnt_next + 1;
                            end else if (cnt_next == 1) begin
                                mem_addr = mem_in[ADDR_WIDTH-1:0];
                                mem_we = 1'b1;
                                mem_data = alu;
                                state_next = EXEC2;
                                cnt_next = cnt_next + 1;
                            end else if (cnt_next == 2) begin
                                state_next = FETCH;
                                cnt_next = 0;
                            end
                        end
                    end
                    STOP: begin
                        if (ir_high[3] == 0) begin 
                            //direktno
                            if (cnt_next == 0) begin 
                                mem_addr = ir_high[2:0];
                                mem_we = 1'b0;
                                state_next = EXEC2;
                                cnt_next = cnt_next + 1;
                            end else if (cnt_next == 1) begin 
                                //provera da li je 0000 operand
                                if (mem_in != 4'b0000) begin
                                    out_next = mem_in; 
                                end 
                                state_next = FINISH;
                                cnt_next = 0;
                            end
                        end else if (ir_high[3] == 1) begin
                            //indirektno
                            if (cnt_next == 0) begin
                                //procitati adresu iz memorije
                                mem_addr = ir_high[6:4];
                                mem_we = 1'b0;
                                state_next = EXEC2;
                                cnt_next = cnt_next + 1;

                            end else if (cnt_next == 1) begin
                                //procitati vrednost iz memorije sa nove adrese
                                mem_addr = mem_in[ADDR_WIDTH-1:0];
                                mem_we = 1'b0;
                                state_next = EXEC2;
                                cnt_next = cnt_next + 1;

                            end else if (cnt_next == 2) begin
                                //poslati na out
                                if (mem_in != 4'b0000) begin
                                    out_next = mem_in; 
                                end 
                                state_next = FINISH;
                                cnt_next = 0;
                            end
                        end
                    end
                endcase
            end 

            FINISH: begin 
                //$finish;
            end
        endcase
    end


endmodule
