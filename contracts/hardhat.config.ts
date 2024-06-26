import { HardhatUserConfig } from "hardhat/config";
import "@nomicfoundation/hardhat-toolbox";
require("dotenv").config();
import "hardhat-contract-sizer";

const config: HardhatUserConfig = {
  solidity: {
    version: "0.8.18",
    settings: {
      optimizer: {
        enabled: true,
        runs: 200,
      },
      viaIR: true,
    },
  },
  defaultNetwork: "hardhat",
  networks: {
    goerli: {
      url: "https://eth-goerli.public.blastapi.io",
      accounts: [process.env.PKEY as string],
    },
    polygon: {
      url: "https://polygon.llamarpc.com",
      accounts: [process.env.PKEY as string],
    },
    mumbai: {
      url: "https://polygon-mumbai-bor.publicnode.com",
      accounts: [process.env.PKEY as string],
    },
    sepolia: {
      url: "https://eth-sepolia.public.blastapi.io",
      accounts: [process.env.PKEY as string],
    }
  },
};

export default config;
