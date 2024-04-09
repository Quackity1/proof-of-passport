import { describe } from 'mocha';
import chai, { assert, expect } from 'chai';
import chaiAsPromised from 'chai-as-promised';
import { groth16 } from 'snarkjs';
import { attributeToPosition } from '../../common/src/constants/constants';
import { getPassportData } from '../../common/src/utils/passportData';
import { generateCircuitInputs } from '../../common/src/utils/generateInputs';
import path from 'path';
import fs from 'fs';
import { PassportData } from '../../common/src/utils/types';
const wasm_tester = require("circom_tester").wasm;

chai.use(chaiAsPromised);

console.log("The following snarkjs error logs are normal and expected if the tests pass.");

async function performFullProve(inputs: any, wasmPath: string, zkeyPath: string) {
    return groth16.fullProve(inputs, wasmPath, zkeyPath);
}

async function verifyProof(vKey: any, publicSignals: any, proof: any) {
    return groth16.verify(vKey, publicSignals, proof);
}

async function testInvalidInputs(inputs: any, wasmPath: string, zkeyPath: string) {
    return groth16.fullProve(inputs, wasmPath, zkeyPath);
}

async function checkConstraints(circuit: any, inputs: any) {
    const w = await circuit.calculateWitness(inputs);
    return circuit.checkConstraints(w);
}

describe('Circuit tests', function () {
    this.timeout(0);

    let inputs: any;
    let passportData: PassportData;

    before(async () => {
        passportData = getPassportData();

        const reveal_bitmap = Array(90).fill('0');
        const address = "0x70997970c51812dc3a010c7d01b50e0d17dc79c8";

        inputs = generateCircuitInputs(
            passportData,
            reveal_bitmap,
            address,
            { developmentMode: true }
        );

        console.log('inputs', inputs);
    });

    describe('Proof', function () {
        it('should prove and verify with valid inputs', async function () {
            const { proof: zk_proof, publicSignals } = await performFullProve(
                inputs,
                "build/proof_of_passport_js/proof_of_passport.wasm",
                "build/proof_of_passport_final.zkey"
            );

            console.log('zk_proof', zk_proof);
            console.log('publicSignals', publicSignals);

            const vKey = JSON.parse(fs.readFileSync("build/proof_of_passport_vkey.json") as unknown as string);
            const verified = await verifyProof(vKey, publicSignals, zk_proof);

            assert(verified == true, 'Should verify');

            console.log('verified', verified);
        });

        // Other tests...
    });

    describe('Selective disclosure', function () {
        async function performSelectiveDisclosure(combination: string[]) {
            const attributeToReveal = Object.keys(attributeToPosition).reduce((acc, attribute) => {
                acc[attribute] = combination.includes(attribute);
                return acc;
            }, {});

            const bitmap = Array(90).fill('0');

            Object.entries(attributeToReveal).forEach(([attribute, reveal]) => {
                if (reveal) {
                    const [start, end] = attributeToPosition[attribute];
                    bitmap.fill('1', start, end + 1);
                }
            });

            inputs = {
                ...inputs,
                reveal_bitmap: bitmap.map(String),
            };

            const { proof, publicSignals } = await performFullProve(
                inputs,
                "build/proof_of_passport_js/proof_of_passport.wasm",
                "build/proof_of_passport_final.zkey"
            );

            console.log('proof done');

            const vKey = JSON.parse(fs.readFileSync("build/proof_of_passport_vkey.json").toString());
            const verified = await verifyProof(vKey, publicSignals, proof);

            assert(verified == true, 'Should verifiable');

            console.log('proof verified');

            const firstThreeElements = publicSignals.slice(0, 3);
            const bytesCount = [31, 31, 26]; // nb of bytes in each of the first three field elements

            const bytesArray = firstThreeElements.flatMap((element: string, index: number) => {
                const bytes = bytesCount[index];
                const elementBigInt = BigInt(element);
                const byteMask = BigInt(255); // 0xFF

                const bytesOfElement = [...Array(bytes)].map((_, byteIndex) => {
                    return (elementBigInt >> (BigInt(byteIndex) * BigInt(8))) & byteMask;
                });

                return bytesOfElement;
            });

            const result = bytesArray.map((byte: bigint) => String.fromCharCode(Number(byte)));

            console.log(result);

            for (let i = 0; i < result.length; i++) {
                if (bitmap[i] == '1') {
                    const char = String.fromCharCode(Number(inputs.mrz[i + 5]));
                    assert(result[i] == char, 'Should reveal the right one');
                } else {
                    assert(result[i] == '\x00', 'Should not reveal');
                }
            }
        }

        const attributeCombinations = [
            ['issuing_state', 'name'],
            ['passport_number', 'nationality', 'date_of_birth'],
            ['gender', 'expiry_date'],
        ];

        attributeCombinations.forEach(combination => {
            it(`Disclosing ${combination.join(", ")}`, async function () {
                await performSelectiveDisclosure(combination);
            });
        });
    });

    describe('Circom tester tests', function () {
        it('should prove and verify with valid inputs', async function () {
            const circuit = await wasm_tester(
                path.join(__dirname, `../circuits/proof_of_passport.circom`),
                { include: ["node_modules"] },
            );
            await checkConstraints(circuit, inputs);
        });
    });

});
